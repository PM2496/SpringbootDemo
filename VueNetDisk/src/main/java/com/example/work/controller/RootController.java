package com.example.work.controller;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.example.work.bean.User;
import com.example.work.mapper.UserMapper;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Slf4j
@RestController
public class RootController {
    @Autowired
    private UserMapper userMapper;

    @PostMapping(value = "/RootShowUsers")
    public String rootShowUsers(HttpServletRequest request) throws JsonProcessingException {
        String email = (String)request.getAttribute("email");
        String username = (String)request.getAttribute("username");
        System.out.println(email+username);

        HashMap<String,Object> hs=new HashMap<>();
        ObjectMapper objectMapper=new ObjectMapper();
        if(email.equals("ROOT@ROOT") && username.equals("Root")){
            List<User> users1 = userMapper.selectList(null);
            hs.put("userInf", users1);
            return objectMapper.writeValueAsString(hs);
        }

        return null;
    }

    @PostMapping(value = "/RootDelUser")
    public String rootDelUser(@RequestBody Map<String,Object> map, HttpServletRequest request) throws JsonProcessingException {
        String email = (String)map.get("delEmail");
        System.out.println(email);
        String realpath = request.getServletContext().getRealPath("/users/" + email + '/');
        File files = new File(realpath);
        File[] file0 = files.listFiles();

        HashMap<String,Object> hs=new HashMap<>();
        ObjectMapper objectMapper=new ObjectMapper();
        try{
            for(File file : file0){
                file.delete();
            }
            files.delete();
        }catch (Exception e){

            hs.put("code", 1);
            return objectMapper.writeValueAsString(hs);
        }

        try {
            Map<String, Object> map1 = new HashMap<>();
            map1.put("email", email);
            userMapper.deleteByMap(map1);
        }catch (Exception e){
            hs.put("code", 2);
            return objectMapper.writeValueAsString(hs);
        }

        List<User> users = userMapper.selectList(null);
        hs.put("userInf", users);
        hs.put("code", 0);
        return objectMapper.writeValueAsString(hs);
    }

    @PostMapping(value = "/RootShowUserFile")
    public String rootShowUserFile(@RequestParam("email") String email, HttpServletRequest request) throws JsonProcessingException {

        HashMap<String,Object> hs=new HashMap<>();
        ObjectMapper objectMapper=new ObjectMapper();

        System.out.println(email);
        String realpath = request.getServletContext().getRealPath("/users/"+email+'/');//获取项目真实地址
        List<String> list = new ArrayList<>();
        File[] file0 = new File(realpath).listFiles();
        for (File file : file0) {
            list.add(file.getName().substring(file.getName().lastIndexOf("\\") + 1));
        }
//        hs.put("status", 200);
        hs.put("files", list);
        return objectMapper.writeValueAsString(hs);
    }

    @PostMapping(value = "/RootAddUser")
    public String rootAddUser(@RequestBody User user, HttpServletRequest request) throws JsonProcessingException {

        QueryWrapper wrapper = new QueryWrapper();
        wrapper.eq("email",user.getEmail());

        HashMap<String,Object> hs=new HashMap<>();
        ObjectMapper objectMapper=new ObjectMapper();

        if(userMapper.exists(wrapper)){
//            log.info(user.getEmail());
//            hs.put("status", 200);
            hs.put("code", 1);
            return objectMapper.writeValueAsString(hs);
        }else {
            String realpath = request.getServletContext().getRealPath("/users/"+user.getEmail()+'/');
            System.out.println(realpath);
            File file = new File(realpath);
            file.mkdirs();
            userMapper.insert(user);
//            hs.put("status", 200);
            hs.put("code", 0);
            List<User> users = userMapper.selectList(null);
            hs.put("userInf", users);
            return objectMapper.writeValueAsString(hs);
        }
    }

    @PostMapping(value = "/RootFileDelete")
    public String fileDelete(@RequestBody Map<String,Object> map, HttpServletRequest request) throws JsonProcessingException {
        List<String> delFiles =(List<String>) map.get("delFiles");

        String email = (String)map.get("email");
        String realpath = request.getServletContext().getRealPath("/users/" + email + '/');
        System.out.println(realpath);

        HashMap<String,Object> hs=new HashMap<>();
        ObjectMapper objectMapper=new ObjectMapper();
        for (String filename:delFiles) {
            File file = new File(realpath + filename);
            try {
                file.delete();
            }catch (Exception e){
                List<String> list = new ArrayList<>();
                File[] file0 = new File(realpath).listFiles();
                if(file0 == null){
//                    hs.put("status", 200);
                    hs.put("code", 1);
                    hs.put("files", null);
                    return objectMapper.writeValueAsString(hs);
                }
                for (File file1 : file0) {
                    list.add(file1.getName().substring(file1.getName().lastIndexOf("\\") + 1));
                }
//                hs.put("status", 200);
                hs.put("code", 1);
                hs.put("file0", file0);
                return objectMapper.writeValueAsString(hs);
            }
        }
        List<String> list = new ArrayList<>();
        File[] file0 = new File(realpath).listFiles();
        if(file0 == null){
//            hs.put("status", 200);
            hs.put("code", 0);
            hs.put("files", null);
            return objectMapper.writeValueAsString(hs);
        }else {
            for (File file1 : file0) {
                list.add(file1.getName().substring(file1.getName().lastIndexOf("\\") + 1));
            }
//            hs.put("status", 200);
            hs.put("code",0);
            hs.put("files", list);
            return objectMapper.writeValueAsString(hs);
        }


    }

    @PostMapping(value = "/RootFileUpload")
    public String upload(@RequestParam("email") String email, @RequestParam("file") MultipartFile file, @RequestParam("filename") String filename, HttpServletRequest request) throws JsonProcessingException {

        System.out.println(email+"=======");
        String realpath = request.getServletContext().getRealPath("/users/"+email+'/');//获取项目真实地址

        System.out.println(realpath);
        String resetfilename;

        HashMap<String,Object> hs=new HashMap<>();
        ObjectMapper objectMapper=new ObjectMapper();

        if(file.getSize()>10*1024*1024){
//            hs.put("status", 200);
            hs.put("code", 4);
            return objectMapper.writeValueAsString(hs);
        }
        if (filename.length()!=0){
            resetfilename = filename;
        }else {
            resetfilename = file.getOriginalFilename();
        }

        String suffix = resetfilename.substring(resetfilename.lastIndexOf(".") + 1);
        System.out.println(suffix);

        if(suffix.equals("jsp")){
//            hs.put("status", 200);
            hs.put("code", 2);
            return objectMapper.writeValueAsString(hs);
        }

        String filerealpath = realpath + "/" + resetfilename;
        File f=new File(filerealpath);

        if(!f.exists()){
            try {
                f.createNewFile();
            } catch (IOException e) {
//                throw new RuntimeException(e);
//                hs.put("status", 200);
                hs.put("code", 5);
                return objectMapper.writeValueAsString(hs);
            }

            try {
                file.transferTo(f);
            } catch (IOException e) {
//                throw new RuntimeException(e);
//                hs.put("status", 200);
                hs.put("coee", 3);
                return objectMapper.writeValueAsString(hs);
            }

//            hs.put("status", 200);
            hs.put("code", 0);

            List<String> list = new ArrayList<>();
            File[] file0 = new File(realpath).listFiles();
            for (File file1 : file0) {
                list.add(file1.getName().substring(file1.getName().lastIndexOf("\\") + 1));
            }
            hs.put("files", list);
            return objectMapper.writeValueAsString(hs);

        } else{
//            hs.put("status", 200);
            hs.put("code", 1);
            return objectMapper.writeValueAsString(hs);
        }
    }

    @PostMapping(value = "/RootFileDownload")
    public void fileDownload(@RequestParam("email") String email, @RequestParam("filename") String filename, HttpServletRequest request, HttpServletResponse response) {

        String realpath = request.getServletContext().getRealPath("/users/" + email + '/');
        System.out.println(realpath+filename);
        File file = new File(realpath + filename);
        //设置编码格式，防止下载的文件内乱码
        response.setCharacterEncoding("UTF-8");
        //获取路径文件对象
        //设置响应头类型，这里可以根据文件类型设置，text/plain、application/vnd.ms-excel等
        response.setHeader("content-type", "application/octet-stream;charset=UTF-8");
        response.setContentType("application/octet-stream;charset=UTF-8");
        //如果不设置响应头大小，可能报错
//        response.addHeader("Content-Length", String.valueOf(file.length()));
//        try {
//            //Content-Disposition的作用：告知浏览器以何种方式显示响应返回的文件，用浏览器打开还是以附件的形式下载到本地保存
//            //attachment表示以附件方式下载   inline表示在线打开   "Content-Disposition: inline; filename=文件名.mp3"
//            // filename表示文件的默认名称，因为网络传输只支持URL编码的相关支付，因此需要将文件名URL编码后进行传输,前端收到后需要反编码才能获取到真正的名称
//
//            response.setHeader("Content-Disposition", "attachment;filename=" + java.net.URLEncoder.encode(filename.trim(), "UTF-8"));
//            System.out.println(java.net.URLEncoder.encode(filename.trim()));
//        } catch (UnsupportedEncodingException e1) {
//            e1.printStackTrace();
//        }
        //初始化文件流字节缓存
        byte[] buff = new byte[1024];
        BufferedInputStream bis = null;
        try {
            //开始写入
            OutputStream os = response.getOutputStream();
            //写入完成，创建文件流
            bis = new BufferedInputStream(new FileInputStream(file));
            // bis.read(data)：将字符读入数组。在某个输入可用、发生I/O错误或者已到达流的末尾前，此方法一直阻塞。
            // 读取的字符数，如果已到达流的末尾，则返回 -1
            int i = bis.read(buff);
            while (i != -1) {
                os.write(buff, 0, buff.length);
                os.flush();
                i = bis.read(buff);
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (bis != null) {
                try {
                    bis.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    @GetMapping(value = "/ShowLogs")
    public String showLogs(){
        return "/logs";
    }
}
