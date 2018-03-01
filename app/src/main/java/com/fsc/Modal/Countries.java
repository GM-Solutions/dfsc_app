package com.fsc.Modal;

/**
 * Created by Admin on 1/11/2018.
 */

public class Countries {
    public String id;
    public String value;
    public String mobile_validation;
    public String flag;

    public Countries(String id, String value, String mobile_validation, String flag) {
        this.id = id;
        this.value = value;
        this.mobile_validation = mobile_validation;
        this.flag = flag;
    }

    @Override
    public String toString() {
        return value;
    }
}
