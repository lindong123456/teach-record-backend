package com.teachrecord.web;

import com.teachrecord.security.AppUserPrincipal;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.server.ResponseStatusException;

public final class SecurityUtils {

    private SecurityUtils() {}

    public static AppUserPrincipal principal() {
        var auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth == null || !(auth.getPrincipal() instanceof AppUserPrincipal p)) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED);
        }
        return p;
    }

    public static long currentTeacherId() {
        AppUserPrincipal p = principal();
        if (p.isParent()) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN);
        }
        return p.getTeacherId();
    }

    public static long currentStudentId() {
        AppUserPrincipal p = principal();
        if (!p.isParent()) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN);
        }
        return p.getStudentId();
    }

    public static long parentTeacherId() {
        AppUserPrincipal p = principal();
        if (!p.isParent()) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN);
        }
        return p.getTeacherId();
    }
}
