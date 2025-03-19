package com.hiroc.rangero.projectMember;

public enum ProjectRole {
    MEMBER,
    ADMIN,
    OWNER;

    public boolean hasPermission(ProjectRole requiredRole){
        if (this==OWNER) return true;
        else if (this==ADMIN && requiredRole!=OWNER) return true;
        return this==requiredRole;
    }

}
