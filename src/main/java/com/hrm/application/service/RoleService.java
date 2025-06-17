package com.hrm.application.service;

import com.hrm.application.entity.ApiResponse;
import com.hrm.application.entity.Permission;
import com.hrm.application.model.Option;
import com.hrm.application.entity.Role;
import com.hrm.application.util.BEClientRestUtil;
import com.hrm.application.util.BEClientUtil;
import com.hrm.application.util.NotificationUtil;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;

import java.util.*;
import java.util.stream.Collectors;

@Service
public class RoleService {

    @Value("${hrm.url}")
    private String backEndDomain;

    private final BEClientRestUtil client;
    public RoleService(BEClientRestUtil client) {
        this.client = client;
    }
    private final static String PERMISSION_SPLIT_SYMBOL = ",";

    public List<Role> getAll() {
        String url = backEndDomain + API.GET_ROLES.getPath();
        ParameterizedTypeReference<ApiResponse<List<Role>>> responseType = new ParameterizedTypeReference<>() {
        };
        ApiResponse<List<Role>> response = client.doGet(url, null, null, responseType);
        if (response != null) {
            return response.getData();
        }
        return new ArrayList<>();
    }

    public List<Option<Integer>> getRoleEnumList() {
        String url = backEndDomain + API.GET_ROLE_OPTIONS.getPath();
        ParameterizedTypeReference<ApiResponse<List<Option<Integer>>>> responseType = new ParameterizedTypeReference<>() {
        };
        ApiResponse<List<Option<Integer>>> response = client.doGet(url, null, null, responseType);
        if (response != null) {
            return response.getData();
        }
        return new ArrayList<>();
    }

    public Role getById(Integer id) {
        String url = backEndDomain + API.QUERY_ROLE.getPath();
        Map<String, Object> pathValues = new LinkedHashMap<>();
        pathValues.put("id", id);
        ParameterizedTypeReference<ApiResponse<Role>> responseType = new ParameterizedTypeReference<>() {
        };

        ApiResponse<Role> response = client.doGet(url, pathValues, null, responseType);
        if (response != null) {
            return response.getData();
        }
        return null;
    }

    public boolean save(Role role) {
        String url = backEndDomain + API.CREATE_ROLE.getPath();
        ParameterizedTypeReference<ApiResponse<Object>> responseType = new ParameterizedTypeReference<>() {
        };

        ApiResponse<Object> response = client.doPostJson(url, null, role, responseType);
        if (response != null) {
            if (response.getCode().equals(0)) {
                NotificationUtil.success(response.getMessage());
                return true;
            }
        }
        NotificationUtil.error(response.getMessage());
        return false;
    }

    public boolean updateRole(Role role) {
        String url = backEndDomain + API.UPDATE_ROLE.getPath();
        ParameterizedTypeReference<ApiResponse<Object>> responseType = new ParameterizedTypeReference<>() {
        };

        ApiResponse<Object> response = client.doPostJson(url, null, role, responseType);
        if (response != null) {
            if (response.getCode().equals(0)) {
                NotificationUtil.success(response.getMessage());
                return true;
            }
        }
        NotificationUtil.error(response.getMessage());
        return false;
    }

    public boolean delete(Role role) {
        String url = backEndDomain + API.DELETE_ROLE.getPath();
        Map<String, Object> pathValues = new LinkedHashMap<>();
        pathValues.put("id", role.getId());
        ParameterizedTypeReference<ApiResponse<Object>> responseType = new ParameterizedTypeReference<>() {
        };

        ApiResponse<Object> response = client.doPostJson(url, null, role, responseType);
        if (response != null) {
            if (response.getCode().equals(0)) {
                NotificationUtil.success(response.getMessage());
                return true;
            }
        }
        NotificationUtil.error(response.getMessage());
        return false;
    }

    public List<Permission> getPermissionList() {
        String url = backEndDomain + API.GET_PERMISSIONS.getPath();
        ParameterizedTypeReference<ApiResponse<List<Permission>>> responseType = new ParameterizedTypeReference<>() {
        };

        ApiResponse<List<Permission>> response = client.doGet(url, null, null, responseType);
        if (response != null) {
            if (response.getCode().equals(0)) {
//                NotificationUtil.success(response.getMessage());
                return response.getData();
            }
        }
        return new ArrayList<>();
    }


    public String mapPermissionsToString(String permissions, Map<String, Permission> codeMap) {
        if (StringUtils.isBlank(permissions) || codeMap == null) {
            return "";
        }
        return Arrays.stream(permissions.split(PERMISSION_SPLIT_SYMBOL))
                .filter(StringUtils::isNotBlank)
                .map(String::trim)
                .map(codeMap::get)
                .filter(Objects::nonNull)
                .map(Permission::getText)
                .filter(Objects::nonNull)
                .reduce((a, b) -> a + PERMISSION_SPLIT_SYMBOL + b)
                .orElse("");
    }

    public Set<Permission> mapPermissionsToSet(String permissions, Map<String, Permission> codeMap) {
        if (StringUtils.isBlank(permissions) || codeMap == null) {
            return new HashSet<>();
        }
        return Arrays.stream(permissions.split(PERMISSION_SPLIT_SYMBOL))
                .filter(StringUtils::isNotBlank)
                .map(String::trim)
                .map(codeMap::get)
                .filter(Objects::nonNull)
                .collect(Collectors.toSet());
    }

    public String transPermissionsSetToCodes(Set<Permission> set) {
        if (set == null) {
            return "";
        }
        return set.stream()
                .map(Permission::getCode)
                .filter(StringUtils::isNotBlank)
                .reduce((a, b) -> a + PERMISSION_SPLIT_SYMBOL + b)
                .orElse("");
    }

    //將平面的 permissionList 封裝成層級結構的 Permission：
    public List<Permission> buildPermissionHierarchy(List<Permission> permissions, Map<String, Permission> permissionMap) {
//        Map<String, Permission> codeToPermissionMap = permissions.stream()
//                .collect(Collectors.toMap(Permission::getCode, p -> p));

        List<Permission> rootPermissions = new ArrayList<>();

        for (Permission permission : permissions) {
            if (permission.getCode().length() == 4) {
                // 根節點
                rootPermissions.add(permission);
            } else {
                // 子節點
                String parentCode = permission.getCode().substring(0, permission.getCode().length() - 3);
                Permission parent = permissionMap.get(parentCode);
                if (parent != null) {
                    parent.getChildren().add(permission);
                }
            }
        }

        return rootPermissions;
    }

    private enum API {

        CREATE_ROLE("/role/create", HttpMethod.POST, MediaType.APPLICATION_JSON),
        UPDATE_ROLE("/role/update", HttpMethod.POST, MediaType.APPLICATION_JSON),
        DELETE_ROLE("/role/delete/{id}", HttpMethod.POST, null),
        GET_ROLES("/role/query", HttpMethod.GET, null),
        QUERY_ROLE("/role/query/{id}", HttpMethod.GET, null),
        GET_ROLE_OPTIONS("/role/getEnumList", HttpMethod.GET, null),

        GET_PERMISSIONS("/menu/menuRoles", HttpMethod.GET, null),


        NONE("", null, null);


        String path;
        HttpMethod method;
        MediaType type;

        API(String path, HttpMethod method, MediaType type) {
            this.path = path;
            this.method = method;
            this.type = type;
        }

        public String getPath() {
            return path;
        }

        public void setPath(String path) {
            this.path = path;
        }

        public HttpMethod getMethod() {
            return method;
        }

        public void setMethod(HttpMethod method) {
            this.method = method;
        }

        public MediaType getType() {
            return type;
        }

        public void setType(MediaType type) {
            this.type = type;
        }

    }
}
