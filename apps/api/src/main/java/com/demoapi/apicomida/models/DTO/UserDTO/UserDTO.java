package com.demoapi.apicomida.models.DTO.UserDTO;

public class UserDTO {
    private Long id;
    private String name;
    private int age;
    private String email;
    private float weight;
    private float height;
    private int exerciseLevel;

    public UserDTO() {
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getAge() {
        return age;
    }

    public void setAge(int age) {
        this.age = age;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public float getWeight() {
        return weight;
    }

    public void setWeight(float weight) {
        this.weight = weight;
    }

    public float getHeight() {
        return height;
    }

    public void setHeight(float height) {
        this.height = height;
    }

    public int getExerciseLevel() {
        return exerciseLevel;
    }

    public void setExerciseLevel(int exerciseLevel) {
        this.exerciseLevel = exerciseLevel;
    }
}
