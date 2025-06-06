package com.jobmatrix.exceptionHandling;

public class SkillNotFoundException extends RuntimeException {

    public SkillNotFoundException(Long skillId) {
        super("Skill not found at given skillId: " + skillId);
    }
}
