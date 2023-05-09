package com.pcs.daejeon.service;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.pcs.daejeon.common.InstagramUtil;
import com.pcs.daejeon.common.Util;
import com.pcs.daejeon.dto.school.MealApiDto;
import com.pcs.daejeon.dto.school.MealDto;
import com.pcs.daejeon.entity.Member;
import com.pcs.daejeon.entity.School;
import com.pcs.daejeon.repository.SchoolRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.*;

@Service
@Transactional
@RequiredArgsConstructor
public class SchoolService {

    private final Util util;
    private final SchoolRepository schoolRepository;

    public List<School> findAllSchool() {
        return schoolRepository.findAll();
    }
    public School findSchool(Long schoolId) {
        Member loginMember = util.getLoginMember();
        if (schoolId == 0 && loginMember != null) {
            schoolId = loginMember.getSchool().getId();
        }

        Optional<School> school = schoolRepository.findById(schoolId);

        if (school.isEmpty()) throw new IllegalStateException("school not found");
        return school.get();
    }

    public MealDto getMealServiceInfo(
            String schoolCode,        // 학교 코드
            String ATPT_OFCDC_SC_CODE // 교육청 코드
    ) throws IOException {
        LocalDateTime now = LocalDateTime.now(java.time.ZoneId.of("Asia/Seoul"));


        if (now.getHour() > 14) {
            now = now.plusDays(1);
        }

        // format 변경
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyyMMdd");
        String today = now.format(formatter);
        String url = "https://open.neis.go.kr/hub/mealServiceDietInfo?" +
                URLEncoder.encode("&Type=json" +
                "&pIndex=1" +
                "&pSize=100" +
                "&ATPT_OFCDC_SC_CODE=" + ATPT_OFCDC_SC_CODE +
                "&SD_SCHUL_CODE=" + schoolCode +
                "&MLSV_FROM_YMD=" + today +
                "&MLSV_TO_YMD=" + today, StandardCharsets.UTF_8);

        HttpURLConnection connection = (HttpURLConnection) new URL(url).openConnection();
        connection.setRequestMethod("GET");

        InputStream responseStream = connection.getInputStream();
        BufferedReader reader = new BufferedReader(new InputStreamReader(responseStream));
        String line;
        StringBuilder response = new StringBuilder();

        while ((line = reader.readLine()) != null) {
            response.append(line);
        }

        reader.close();
        responseStream.close();

        ObjectMapper objectMapper = new ObjectMapper();
        LinkedHashMap map = objectMapper.readValue(response.toString(), LinkedHashMap.class);
        if (map.get("RESULT") != null) {
            return new MealDto();
        }

        ArrayList mealServiceDietInfo = objectMapper.readValue(objectMapper.writeValueAsString(map.get("mealServiceDietInfo")), ArrayList.class);
        LinkedHashMap mealInfo = objectMapper.readValue(objectMapper.writeValueAsString(mealServiceDietInfo.get(1)), LinkedHashMap.class);
        List<MealApiDto> rows = objectMapper.readValue(
                objectMapper.writeValueAsString(mealInfo.get("row")),
                new TypeReference<List<MealApiDto>>() {}
        );

        MealDto meals = new MealDto();

        rows.stream().forEach(val -> {
            Object[] dish = Arrays.stream(val.getDishName().split("<br/>")).map(br -> br.split(" ")[0]).toArray();

            String mealCode = val.getMealCode();

            if (Objects.equals(mealCode, "1"))      meals.setBreakfast(dish);
            else if (Objects.equals(mealCode, "2")) meals.setLunch(dish);
            else if (Objects.equals(mealCode, "3")) meals.setDinner(dish);
        });

        return meals;
    }

    /**
     *
     * @param schoolName
     * @param location
     * @return 0: school code / 1: locate code
     * @throws IOException
     */
    public String[] getSchoolCodes(String schoolName, String location) throws IOException {
        String url = "https://open.neis.go.kr/hub/schoolInfo?" +
                "Type=json" +
                "&pSize=3" +
                URLEncoder.encode(
                        "&SCHUL_NM=" +schoolName +
                        "&LCTN_SC_NM=" + location,
                        StandardCharsets.UTF_8); // 한글 인코딩

        URL apiUrl = new URL(url);
        HttpURLConnection connection = (HttpURLConnection) apiUrl.openConnection();
        connection.setRequestMethod("GET");

        int responseCode = connection.getResponseCode();
        if (responseCode == HttpURLConnection.HTTP_OK) {
            BufferedReader in = new BufferedReader(
                    new InputStreamReader(connection.getInputStream()));
            String inputLine;
            StringBuilder response = new StringBuilder();
            while ((inputLine = in.readLine()) != null) {
                response.append(inputLine);
            }
            in.close();
            ObjectMapper objectMapper = new ObjectMapper();
            LinkedHashMap map = objectMapper.readValue(response.toString(), LinkedHashMap.class);
            ArrayList schoolInfoList = objectMapper.readValue(objectMapper.writeValueAsString(map.get("schoolInfo")), ArrayList.class);
            LinkedHashMap schoolInfo = objectMapper.readValue(objectMapper.writeValueAsString(schoolInfoList.get(1)), LinkedHashMap.class);
            LinkedHashMap[] rows = objectMapper.readValue(objectMapper.writeValueAsString(schoolInfo.get("row")), LinkedHashMap[].class);
            LinkedHashMap code = objectMapper.readValue(objectMapper.writeValueAsString(rows[0]), LinkedHashMap.class);

            return new String[]{
                    code.get("SD_SCHUL_CODE").toString(),
                    code.get("ATPT_OFCDC_SC_CODE").toString()};
        } else {
            throw new IOException("Error retrieving school information: " + responseCode);
        }
    }

    public void updateInstaInfo(String instaId, String instaPw) throws Exception {
        Member loginMember = util.getLoginMember();

        School school = schoolRepository.findById(loginMember.getSchool().getId()).get();
        school.setIsableInstagram(true);
        school.updateInstagram(instaId, instaPw);
    }

    public void disableInsta() throws Exception {
        Member loginMember = util.getLoginMember();

        School school = schoolRepository.findById(loginMember.getSchool().getId()).get();
        school.setIsableInstagram(false);
        school.updateInstagram("", "");
    }

    public void activeMealUpload() {
        Member loginMember = util.getLoginMember();

        School school = schoolRepository.findById(loginMember.getSchool().getId()).get();

        school.setUploadMeal(true);
    }

    public void deactivateMealUpload() {
        Member loginMember = util.getLoginMember();

        School school = schoolRepository.findById(loginMember.getSchool().getId()).get();

        school.setUploadMeal(false);
    }

    @Scheduled(fixedDelay = 3600000) // 1시간 반복
    public void uploadMeal() {
        int now = LocalDateTime.now(ZoneId.of("Asia/Seoul")).getHour();
        if (!(now == 19 || now == 8 || now == 13)) return;

        schoolRepository.findAllByUploadMealIsTrue().forEach(school -> {
            try {
                MealDto mealServiceInfo = getMealServiceInfo(school.getCode(), school.getLocationCode());
                InstagramUtil instagramUtil = new InstagramUtil();

                if (now == 19 && mealServiceInfo.getBreakfast() != null) {
                    instagramUtil.mealUploadCaption(mealServiceInfo.getBreakfast());
                }
                if (now == 8 && mealServiceInfo.getLunch() != null) {
                    instagramUtil.mealUploadCaption(mealServiceInfo.getLunch());
                }
                if (now == 13 && mealServiceInfo.getDinner() != null) {
                    instagramUtil.mealUploadCaption(mealServiceInfo.getDinner());
                }

                instagramUtil.uploadMeal(
                        school.getInstaId(),
                        school.getInstaPwd(),
                        school.getSalt());
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        });
    }
}
