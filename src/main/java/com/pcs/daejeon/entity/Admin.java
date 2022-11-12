package com.pcs.daejeon.entity;

import com.pcs.daejeon.entity.type.AdminTier;
import com.pcs.daejeon.entity.type.AuthType;

import javax.persistence.*;

@Entity
public class Admin {

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "admin_id")
    private Long id;

    private AdminTier tier;
}
