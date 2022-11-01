package com.caston.create_mvc.service;

import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletResponse;
import java.util.List;

public interface CreateMVCService {
    List<String> parseSQL(MultipartFile file);

    void genZip(String author, String parent, List<String> sqlNames, HttpServletResponse response);

    void dropTable(List<String> sqlNames);
}
