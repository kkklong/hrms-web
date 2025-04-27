package com.hrm.application.entity;

public class Role {
    /**
     * 角色ID(自動產生)
     */
    private Integer id;

    /**
     * 人資,一般員工,主管,技術長,助理,老闆,其他
     */
    private String roleName;

    /**
     * 可使用的菜單權限
     */
    private String menuPermission;


    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getRoleName() {
        return roleName;
    }

    public void setRoleName(String roleName) {
        this.roleName = roleName;
    }

    public String getMenuPermission() {
        return menuPermission;
    }

    public void setMenuPermission(String menuPermission) {
        this.menuPermission = menuPermission;
    }
}
