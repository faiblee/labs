package ru.ssau.tk.faible.labs.DTO;

public class UserPutDTO {
    private String username;
    private String old_password;
    private String new_password;
    private String factory_type;

    public UserPutDTO(String username, String old_password, String new_password, String factory_type) {
        this.username = username;
        this.old_password = old_password;
        this.new_password = new_password;
        this.factory_type = factory_type;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getOld_password() {
        return old_password;
    }

    public void setOld_password(String old_password) {
        this.old_password = old_password;
    }

    public String getNew_password() {
        return new_password;
    }

    public void setNew_password(String new_password) {
        this.new_password = new_password;
    }

    public String getFactory_type() {
        return factory_type;
    }

    public void setFactory_type(String factory_type) {
        this.factory_type = factory_type;
    }
}
