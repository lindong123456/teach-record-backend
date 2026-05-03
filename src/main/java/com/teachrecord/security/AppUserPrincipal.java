package com.teachrecord.security;

import java.util.Collections;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

public class AppUserPrincipal implements UserDetails {

    public static final String ROLE_TEACHER = "ROLE_TEACHER";
    public static final String ROLE_PARENT = "ROLE_PARENT";

    private final long teacherId; // 0 for parent if we only need student? Parent has student's teacher
    private final long studentId; // 0 for teacher
    private final String name;
    private final String password; // N/A for JWT, use empty
    private final boolean parent;

    public AppUserPrincipal(long teacherId, long studentId, String name, boolean parent) {
        this.teacherId = teacherId;
        this.studentId = studentId;
        this.name = name;
        this.password = "";
        this.parent = parent;
    }

    public long getTeacherId() {
        return teacherId;
    }

    public long getStudentId() {
        return studentId;
    }

    public boolean isParent() {
        return parent;
    }

    @Override
    public java.util.Collection<? extends GrantedAuthority> getAuthorities() {
        String r = parent ? ROLE_PARENT : ROLE_TEACHER;
        return Collections.singletonList(new SimpleGrantedAuthority(r));
    }

    @Override
    public String getPassword() {
        return password;
    }

    @Override
    public String getUsername() {
        return name;
    }

    @Override
    public boolean isAccountNonExpired() {
        return true;
    }

    @Override
    public boolean isAccountNonLocked() {
        return true;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }

    @Override
    public boolean isEnabled() {
        return true;
    }
}
