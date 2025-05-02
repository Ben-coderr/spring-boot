package com.school.common.enums;
public enum Role {
    ADMIN, TEACHER, STUDENT, PARENT;

    /** Spring Security requires the “ROLE_” prefix for `hasRole()` checks. */
    public String asAuthority() {           // e.g. ROLE_ADMIN
        return "ROLE_" + name();
    }
}
