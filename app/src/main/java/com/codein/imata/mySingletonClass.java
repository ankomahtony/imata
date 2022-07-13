package com.codein.imata;

class MySingletonClass {

    private static MySingletonClass instance;

    public static MySingletonClass getInstance() {
        if (instance == null)
            instance = new MySingletonClass();
        return instance;
    }

    private MySingletonClass() {
    }

    private Integer user_id;

    public Integer getValue() {
        return user_id;
    }

    public void setValue(Integer value) {
        this.user_id = value;
    }
}