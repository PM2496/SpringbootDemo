package com.example.work.controller;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.example.work.bean.User;
import com.example.work.mapper.UserMapper;

import com.example.work.utils.TokenUtil;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import lombok.extern.slf4j.Slf4j;

import org.springframework.beans.factory.annotation.Autowired;

import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Slf4j
@RestController
public class IndexController {

    @Autowired
    private UserMapper userMapper;
    @PostMapping(value = "/register")
    public String register(@RequestBody User user, HttpSession session, HttpServletResponse response) throws JsonProcessingException {

        QueryWrapper wrapper = new QueryWrapper();
        wrapper.eq("email",user.getEmail());

        HashMap<String,Object> hs=new HashMap<>();
        ObjectMapper objectMapper=new ObjectMapper();

        if(userMapper.exists(wrapper)){
            log.info(user.getEmail());
//            hs.put("status", 200);
            hs.put("code", 3);
            return objectMapper.writeValueAsString(hs);
        }else {
            String realpath = session.getServletContext().getRealPath("/users/"+user.getEmail()+'/');
            System.out.println(realpath);
            File file = new File(realpath);
            file.mkdirs();
            userMapper.insert(user);
//            hs.put("status", 200);
            hs.put("code", 0);
            return objectMapper.writeValueAsString(hs);
        }
    }

    @PostMapping(value = "/login")
    public String checkUser(@RequestBody Map<String,Object> map, HttpSession session) throws JsonProcessingException {
        //??????????????????????????????
        String email = (String)map.get("email");
        String password = (String)map.get("password");

        HashMap<String,Object> hs=new HashMap<>();
        ObjectMapper objectMapper=new ObjectMapper();
        if (email.equals("ROOT@ROOT") && password.equals("QxbS2*F4y15J78=TrsB!3mY0-+nv.uZD")){
//            hs.put("status",200);
            String token= TokenUtil.sign(email, "Root");
            System.out.println(token+"------------------");
            hs.put("token",token);
            hs.put("code", 4);
            return objectMapper.writeValueAsString(hs);
        }
//        String realpath = session.getServletContext().getRealPath("/WEB-INF/");
//        System.out.println(realpath+"=============");
        System.out.println(email+password);
//        HashMap<String, Object> map1 = new HashMap<>();
        map.put("email",email);
        map.put("password", password);

        List<User> users = userMapper.selectByMap(map);
//        log.info("========" + users.get(0).getName());

        if(!users.isEmpty()){
            System.out.println("success");
//            User user1 = users.get(0);
//            //????????????????????????????????????
//            session.setAttribute("name",users.get(0).getName());
//
//            //????????????????????????main.html;  ?????????????????????????????????
//            return "redirect:/filepage.html";
            String token= TokenUtil.sign(email, users.get(0).getName());
            System.out.println(token+"------------------");
            hs.put("token",token);
//            hs.put("status", 200);
            hs.put("code", 0);
            hs.put("email", email);
            hs.put("username", users.get(0).getName());

            String realpath = session.getServletContext().getRealPath("/users/"+email+'/');//????????????????????????
            List<String> list = new ArrayList<>();
            File[] file0 = new File(realpath).listFiles();
            if(file0 != null){
                for (File file : file0) {
                    list.add(file.getName().substring(file.getName().lastIndexOf("\\") + 1));
                }
                hs.put("filelist", list);
            }else {
                hs.put("filelist", null);
            }

            return objectMapper.writeValueAsString(hs);
        }else {
//            model.addAttribute("msg","??????????????????");
            //??????????????????
            hs.put("code", 1);
            return objectMapper.writeValueAsString(hs);
        }
    }

    @PostMapping(value = "/filelist")
    public String filePage(HttpServletRequest request) throws JsonProcessingException {
        String email = (String)request.getAttribute("email");
        String username = (String)request.getAttribute("username");

        HashMap<String,Object> hs=new HashMap<>();
        ObjectMapper objectMapper=new ObjectMapper();

        String realpath = request.getServletContext().getRealPath("/users/"+email+'/');//????????????????????????
        List<String> list = new ArrayList<>();
        File[] file0 = new File(realpath).listFiles();
        for (File file : file0) {
            list.add(file.getName().substring(file.getName().lastIndexOf("\\") + 1));
        }
//        hs.put("status", 200);
        hs.put("username", username);
        hs.put("files", list);
        return objectMapper.writeValueAsString(hs);
    }

    @PostMapping(value = "/upload")
    public String upload(@RequestParam("file") MultipartFile file, @RequestParam("filename") String filename, HttpServletRequest request) throws JsonProcessingException {
        String email = (String) request.getAttribute("email");
        System.out.println(email+"=======");
        String realpath = request.getServletContext().getRealPath("/users/"+email+'/');//????????????????????????

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

    @PostMapping(value = "/fileDownload")
    public void fileDownload(@RequestParam("filename") String filename, HttpServletRequest request, HttpServletResponse response) {
        String email = (String) request.getAttribute("email");
        String realpath = request.getServletContext().getRealPath("/users/" + email + '/');
        System.out.println(realpath+filename);
        File file = new File(realpath + filename);
        //???????????????????????????????????????????????????
        response.setCharacterEncoding("UTF-8");
        //????????????????????????
        //???????????????????????????????????????????????????????????????text/plain???application/vnd.ms-excel???
        response.setHeader("content-type", "application/octet-stream;charset=UTF-8");
        response.setContentType("application/octet-stream;charset=UTF-8");
        //?????????????????????????????????????????????
//        response.addHeader("Content-Length", String.valueOf(file.length()));
//        try {
//            //Content-Disposition???????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????
//            //attachment???????????????????????????   inline??????????????????   "Content-Disposition: inline; filename=?????????.mp3"
//            // filename?????????????????????????????????????????????????????????URL????????????????????????????????????????????????URL?????????????????????,????????????????????????????????????????????????????????????
//
//            response.setHeader("Content-Disposition", "attachment;filename=" + java.net.URLEncoder.encode(filename.trim(), "UTF-8"));
//            System.out.println(java.net.URLEncoder.encode(filename.trim()));
//        } catch (UnsupportedEncodingException e1) {
//            e1.printStackTrace();
//        }
        //??????????????????????????????
        byte[] buff = new byte[1024];
        BufferedInputStream bis = null;
        try {
            //????????????
            OutputStream os = response.getOutputStream();
            //??????????????????????????????
            bis = new BufferedInputStream(new FileInputStream(file));
            // bis.read(data)?????????????????????????????????????????????????????????I/O???????????????????????????????????????????????????????????????
            // ???????????????????????????????????????????????????????????? -1
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
    @PostMapping(value = "/fileDelete")
    public String fileDelete(@RequestBody Map<String,Object> map, HttpServletRequest request) throws JsonProcessingException {
        List<String> delFiles =(List<String>) map.get("delFiles");
        String email = (String)request.getAttribute("email");
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
}
