package com.kjmaster.resonantia.tileentity;

import java.lang.annotation.*;

@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.FIELD})
@Inherited
public @interface ResonantiaCap {
    ResonantiaCapTypes type();
}

