package com.hrm.application.service;

import com.hrm.application.entity.ApiResponse;
import com.hrm.application.entity.Employee;
import com.hrm.application.entity.Notice;
import com.hrm.application.entity.UserInfo;
import com.hrm.application.model.NameAddedByteArrayResource;
import com.hrm.application.model.Option;
import com.hrm.application.util.BEClientUtil;
import com.hrm.application.util.NotificationUtil;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.http.client.MultipartBodyBuilder;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;

import java.util.*;

@Service
public class NoticeService {

    @Value("${hrm.url}")
    private String backEndDomain;


    // 查詢所有通知與公告
    public List<Notice> getNoticeList() {
        String url = backEndDomain + API.GET_NOTICES.getPath();
        BEClientUtil client = new BEClientUtil(WebClient.builder().build());
        ParameterizedTypeReference<ApiResponse<List<Notice>>> responseType = new ParameterizedTypeReference<>() {
        };

        ApiResponse<List<Notice>> response = client.doGet(url, null, null, responseType);
        if (response != null) {
            return response.getData();
        }
        return new ArrayList<>();
    }

    public List<Option<Byte>> getNoticeStatusOptionList() {
        String url = backEndDomain + API.GET_NOTICE_STATUS_OPTIONS.getPath();
        BEClientUtil client = new BEClientUtil(WebClient.builder().build());
        ParameterizedTypeReference<ApiResponse<List<Option<Byte>>>> responseType = new ParameterizedTypeReference<>() {
        };

        ApiResponse<List<Option<Byte>>> response = client.doGet(url, null, null, responseType);
        if (response != null && response.getData() != null) {
            return response.getData();
        }
        return new ArrayList<>();
    }

    public List<Option<String>> getNoticeTypeOptionList() {
        String url = backEndDomain + API.GET_NOTICE_TYPE_OPTIONS.getPath();
        BEClientUtil client = new BEClientUtil(WebClient.builder().build());
        ParameterizedTypeReference<ApiResponse<List<Option<String>>>> responseType = new ParameterizedTypeReference<>() {
        };

        Map<String, String> requestBody = new HashMap<>();

        ApiResponse<List<Option<String>>> response = client.doGet(url, null, null, responseType);
        if (response != null && response.getData() != null) {
            return response.getData();
        }
        return new ArrayList<>();
    }

    public boolean createNotice(Notice notice, byte[] fileBytes, String fileName) {
        String url = backEndDomain + API.CREATE_NOTICE.getPath();
        BEClientUtil client = new BEClientUtil(WebClient.builder().build());
        ParameterizedTypeReference<ApiResponse<Notice>> responseType = new ParameterizedTypeReference<>() {
        };

        MultipartBodyBuilder builder = new MultipartBodyBuilder();
        builder.part("noticeBO", notice);
        if (fileBytes != null) {
            builder.part("file", new ByteArrayResource(fileBytes) {
                @Override
                public String getFilename() {
                    return fileName;
                }
            });
        }
        ApiResponse<Notice> response = client.doPostMultipart(url,null, builder.build(), responseType);
        if (response != null) {
            if (response.getCode().equals(0)) {
                NotificationUtil.success(response.getMessage());
                return true;
            }
        }
        NotificationUtil.error(response.getMessage());
        return false;
    }

    public boolean updateNotice(Notice notice, byte[] fileBytes, String fileName) {
        String url = backEndDomain + API.UPDATE_NOTICE.getPath();
        BEClientUtil client = new BEClientUtil(WebClient.builder().build());
        ParameterizedTypeReference<ApiResponse<Notice>> responseType = new ParameterizedTypeReference<>() {
        };

        MultipartBodyBuilder builder = new MultipartBodyBuilder();
        builder.part("noticeBO", notice);
        if (fileBytes != null) {
            builder.part("file", new ByteArrayResource(fileBytes) {
                @Override
                public String getFilename() {
                    return fileName;
                }
            });
        }
        ApiResponse<Notice> response = client.doPostMultipart(url,null, builder.build(), responseType);
        if (response != null) {
            if (response.getCode().equals(0)) {
                NotificationUtil.success(response.getMessage());
                return true;
            }
        }
        NotificationUtil.error(response.getMessage());
        return false;
    }

    public boolean deleteNotice(Notice notice) {
        String url = backEndDomain + API.DELETE_NOTICE.getPath();
        BEClientUtil client = new BEClientUtil(WebClient.builder().build());
        Map<String, Object> pathValues = new LinkedHashMap<>();
        pathValues.put("id", notice.getId());
        ParameterizedTypeReference<ApiResponse<Object>> responseType = new ParameterizedTypeReference<>() {
        };

        ApiResponse<Object> response = client.doPostJson(url,pathValues, null, responseType);
        if (response != null) {
            if (response.getCode().equals(0)) {
                NotificationUtil.success(response.getMessage());
                return true;
            }
        }
        NotificationUtil.error(response.getMessage());
        return false;
    }

    public boolean deleteBatchNotice(List<Integer> noticeIds) {
        String url = backEndDomain + API.DELETE_NOTICES.getPath();
        BEClientUtil client = new BEClientUtil(WebClient.builder().build());
        ParameterizedTypeReference<ApiResponse<Object>> responseType = new ParameterizedTypeReference<>() {
        };

        ApiResponse<Object> response = client.doPostJson(url, null, noticeIds, responseType);
        if (response != null) {
            if (response.getCode().equals(0)) {
                NotificationUtil.success(response.getMessage());
                return true;
            }
        }
        NotificationUtil.error(response.getMessage());
        return false;
    }

    public boolean saveAndSendNotice(Notice notice, byte[] fileBytes, String fileName) {
        String url = backEndDomain + API.SAVE_AND_SEND_NOTICE.getPath();
        BEClientUtil client = new BEClientUtil(WebClient.builder().build());
        ParameterizedTypeReference<ApiResponse<Notice>> responseType = new ParameterizedTypeReference<>() {
        };

        MultipartBodyBuilder builder = new MultipartBodyBuilder();
        builder.part("noticeBO", notice);
        if (fileBytes != null) {
            builder.part("file", new ByteArrayResource(fileBytes) {
                @Override
                public String getFilename() {
                    return fileName;
                }
            });
        }
        ApiResponse<Notice> response = client.doPostMultipart(url,null, builder.build(), responseType);
        if (response != null) {
            if (response.getCode().equals(0)) {
                NotificationUtil.success(response.getMessage());
                return true;
            }
        }
        NotificationUtil.error(response.getMessage());
        return false;
    }

    public boolean enableNotice(Integer noticeId) {
        String url = backEndDomain + API.SET_NOTICE_ENABLE.getPath();
        BEClientUtil client = new BEClientUtil(WebClient.builder().build());
        Map<String, Object> pathValues = new LinkedHashMap<>();
        pathValues.put("id", noticeId);
        ParameterizedTypeReference<ApiResponse<Employee>> responseType = new ParameterizedTypeReference<>() {
        };
        ApiResponse<Employee> response = client.doPostJson(url,pathValues, null, responseType);
        if (response != null) {
            if (response.getCode().equals(0)) {
                NotificationUtil.success(response.getMessage());
                NotificationUtil.success("成功啟用");
                return true;
            }
        }
        NotificationUtil.error(response.getMessage());
        return false;
    }

    public boolean disableNotice(Integer noticeId) {
        String url = backEndDomain + API.SET_NOTICE_DISABLE.getPath();
        BEClientUtil client = new BEClientUtil(WebClient.builder().build());
        Map<String, Object> pathValues = new LinkedHashMap<>();
        pathValues.put("id", noticeId);
        ParameterizedTypeReference<ApiResponse<Employee>> responseType = new ParameterizedTypeReference<>() {
        };
        ApiResponse<Employee> response = client.doPostJson(url,pathValues, null, responseType);
        if (response != null) {
            if (response.getCode().equals(0)) {
                NotificationUtil.success(response.getMessage());
                NotificationUtil.success("成功停用");
                return true;
            }
        }
        NotificationUtil.error(response.getMessage());
        return false;
    }


    private enum API {
        GET_NOTICES("/notice/getAllNotices", HttpMethod.GET, null),
        CREATE_NOTICE("/notice/create", HttpMethod.POST, MediaType.MULTIPART_FORM_DATA),
        UPDATE_NOTICE("/notice/update", HttpMethod.POST, MediaType.MULTIPART_FORM_DATA),
        SAVE_AND_SEND_NOTICE("/notice/saveAndSend", HttpMethod.POST, MediaType.MULTIPART_FORM_DATA),
        DELETE_NOTICE("/notice/delete/{id}", HttpMethod.POST, null),
        DELETE_NOTICES("/notice/deleteBatch", HttpMethod.POST, MediaType.APPLICATION_JSON),
        SET_NOTICE_ENABLE("/notice/enable/{id}", HttpMethod.POST, MediaType.APPLICATION_JSON),
        SET_NOTICE_DISABLE("/notice/disable/{id}", HttpMethod.POST, MediaType.APPLICATION_JSON),

        // ---- enum ----
        GET_NOTICE_STATUS_OPTIONS("/enum/getNoticeStatus", HttpMethod.GET, null),
        GET_NOTICE_TYPE_OPTIONS("/enum/getNoticeType", HttpMethod.GET, null),


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
