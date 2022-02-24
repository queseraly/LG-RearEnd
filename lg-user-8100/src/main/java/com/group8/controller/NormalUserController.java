package com.group8.controller;

import cn.hutool.captcha.CaptchaUtil;
import cn.hutool.captcha.CircleCaptcha;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.group8.dto.BrowseHistory;
import com.group8.dto.UploadImg;
import com.group8.dto.UserLoginForm;
import com.group8.dto.UserQueryCondition;
import com.group8.entity.*;
//import com.group8.feignClient.TravelNoteClient;
import cn.hutool.core.util.ObjectUtil;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.group8.dto.UserCollects;
import com.group8.service.NormalUserService;
import com.group8.util.CommonUtils;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.context.config.annotation.RefreshScope;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import java.io.IOException;
import java.util.List;

/**
 * @author laiyong
 * @date 2022/2/17 10:49 星期四
 * @apiNote 普通用户Controller
 */
@Api(tags = "普通用户管理")
@RestController
@RefreshScope
@RequestMapping("/nUser")
public class NormalUserController {

    @Autowired
    NormalUserService normalUserService;

    //@Autowired
    //TravelNoteClient tourNoteClient;

    CircleCaptcha captcha;


    /**
     * 用户注册
     *
     * @param lgNormalUser
     * @return 返回注册结果
     */
    @PostMapping("/register")
    @ApiOperation(value = "用户注册", notes = "用户注册")
    public ResponseEntity<String> register(@RequestBody LgNormalUser lgNormalUser) {
        LgNormalUser normalUser = normalUserService.checkUserName(lgNormalUser.getUserName());
        if (normalUser == null) {
            int i = normalUserService.addNormalUser(lgNormalUser);
            if (i == 1) {
                return new ResponseEntity<>(200, "注册成功！请前往邮箱进行进一步验证！", "");
            } else {
                return new ResponseEntity<>(500, "注册失败！", "");
            }
        } else {
            return new ResponseEntity<>(500, "用户名已存在！请重新输入！", "");
        }
    }

    /**
     * 用户名重复验证
     *
     * @param userName 待注册用户名
     * @return 返回验证结果
     */
    @PostMapping("/register/{userName}")
    @ApiOperation(value = "用户名验证", notes = "验证用户名是否重复")
    public ResponseEntity<String> checkUserName(@PathVariable("userName") String userName) {
        LgNormalUser normalUser = normalUserService.checkUserName(userName);
        if (normalUser == null) {
            return new ResponseEntity<>(200, "用户名可用", "");
        } else {
            return new ResponseEntity<>(500, "用户名已存在！请重新输入！", "");
        }
    }

    /**
     * 用户登陆获取token
     *
     * @param userLoginForm 用户信息类
     * @return 登陆结果
     */
    @PostMapping("/login")
    @ApiOperation(value = "用户登陆", notes = "用户登陆")
    public ResponseEntity<LgNormalUser> login(@RequestBody UserLoginForm userLoginForm) {
        LgNormalUser normalUser = normalUserService.login(userLoginForm);
        if (normalUser != null) {
            if (normalUser.getUserStatus().equals("1")){
                return new ResponseEntity<>(200, "登陆成功！", normalUser);
            }else {
                return new ResponseEntity<>(500, "账户未激活！", null);
            }
        } else {
            return new ResponseEntity<>(500, "用户名或密码错误！", null);
        }
    }

    /**
     * 根据token获取用户信息
     *
     * @param token 用户token
     * @return 用户信息
     */
    @PostMapping("/getInfo/{token}")
    @ApiOperation(value = "获取用户信息", notes = "根据token获取用户信息")
    public ResponseEntity<LgNormalUser> getInfo(@PathVariable("token") String token) {
        LgNormalUser normalUser = normalUserService.getInfo(token);
        if (normalUser != null) {
            return new ResponseEntity<>(200, "获取成功！", normalUser);
        } else {
            return new ResponseEntity<>(500, "获取失败！", null);
        }
    }
    /**
     * 用户登出
     *
     * @param token 用户信息token
     * @return 登出结果
     */
    @PostMapping("/logout/{token}")
    @ApiOperation(value = "用户登出", notes = "用户登出")
    public ResponseEntity<String> logout(@PathVariable("token") String token) {
        boolean flag = normalUserService.logout(token);
        if (flag) {
            return new ResponseEntity<>(200, "登出成功！", "");
        } else {
            return new ResponseEntity<>(500, "登出失败！", "");
        }
    }


    /**
     * 查询所有普通用户
     *
     * @return 用户集合
     */
    @GetMapping("/findAll")
    @ApiOperation(value = "用户查询", notes = "根据条件查询用户并分页")
    public ResponseEntity<PageInfo<LgNormalUser>> findByCondition(@RequestBody UserQueryCondition<LgNormalUser> userQueryCondition) {
        PageHelper.startPage(userQueryCondition.getPage(), userQueryCondition.getLimit());
        List<LgNormalUser> lgNormalUserList = normalUserService.findByCondition(userQueryCondition.getUser());
        PageInfo<LgNormalUser> pageInfo = new PageInfo<>(lgNormalUserList);
        return new ResponseEntity<>(200, "查询成功！", pageInfo);
    }

    /**
     * 查询单个用户
     *
     * @return 用户对象
     */
    @GetMapping("/findById/{id}")
    @ApiOperation(value = "用户查询", notes = "根据id查询用户")
    public ResponseEntity<LgNormalUser> findById(@PathVariable("id") int id) {
        LgNormalUser normalUser = normalUserService.findById(id);
        return new ResponseEntity<>(200, "查询成功！", normalUser);
    }

    /**
     * 更新用户信息
     *
     * @param lgNormalUser 更新后的用户对象
     * @return 更新结果
     */
    @PostMapping("/update")
    @ApiOperation(value = "用户更新", notes = "根据id更新用户信息")
    public ResponseEntity<String> update(@RequestBody LgNormalUser lgNormalUser) {
        int i = normalUserService.update(lgNormalUser);
        if (i > 0) {
            return new ResponseEntity<>(200, "修改成功！", "");
        } else {
            return new ResponseEntity<>(500, "修改失败！", "");
        }
    }

    @PostMapping("/updateHeadImg")
    @ApiOperation(value = "修改头像", notes = "根据id修改用户头像")
    public String updateHeadImg(@RequestParam("id") int id,
                                                @RequestParam("file") MultipartFile file) throws IOException {
        //如果文件不为空，上传
        if (!file.isEmpty()){
            UploadImg uploadImg = new UploadImg(id, file);
            return normalUserService.updateHeadImg(uploadImg);
        }else {
            return "请选择文件！";
        }
    }

    /**
     * 删除用户，用于注销用户的账户，删除用户信息
     *
     * @param id 用户id
     * @return 删除结果
     */
    @PostMapping("/deleteById/{id}")
    @ApiOperation(value = "用户删除", notes = "根据id删除用户")
    public ResponseEntity<String> deleteById(@PathVariable("id") int id) {
        int i = normalUserService.deleteById(id);
        if (i > 0) {
            return new ResponseEntity<>(200, "删除成功！", "");
        } else {
            return new ResponseEntity<>(500, "删除失败！", "");
        }
    }

    /**
     * 根据激活码激活账户
     *
     * @param code 用户唯一激活码
     * @return 验证结果
     */
    @RequestMapping("/activeUser")
    @ApiOperation(value = "账户激活", notes = "根据激活码验证用户")
    public ResponseEntity<String> checkActiveCode(String code) {
        boolean b = normalUserService.checkActiveCode(code);
        if (b) {
            return new ResponseEntity<>(200, "验证成功！", "");
        } else {
            return new ResponseEntity<>(200, "验证失败！", "");
        }
    }

    @PostMapping("/browse")
    @ApiOperation(value = "记录浏览历史", notes = "查询详情后记录进浏览历史中")
    public void browse() {
        LgScenicspot lgScenicspot = new LgScenicspot();
        BrowseHistory<LgScenicspot> browseHistory = new BrowseHistory<LgScenicspot>(1, lgScenicspot);
        normalUserService.browse(browseHistory.getUserId(), browseHistory.getBrowsed());
    }

    @PostMapping("/selectBrowsed/{userId}")
    @ApiOperation(value = "查询浏览历史", notes = "查询详情后展示浏览历史")
    public ResponseEntity<List<Object>> selectBrowsed(@PathVariable("userId") long userId) {
        List<Object> list = normalUserService.selectBrowsed(userId);
        return new ResponseEntity<>(200, "查询成功", list);
    }


    @GetMapping("/test")
    public ResponseEntity<String> test() {
        //定义图形验证码的长、宽、验证码字符数、干扰元素个数
        captcha = CaptchaUtil.createCircleCaptcha(200, 100, 4, 20);
        //CircleCaptcha captcha = new CircleCaptcha(200, 100, 4, 20);
        //图形验证码写出，可以写出到文件，也可以写出到流
        captcha.write("/Users/Comme_moi/Desktop/circle.png");
        return null;
    }


    @GetMapping("/test1/{code}")
    public ResponseEntity<String> test1(@PathVariable("code") String code) {
        //验证图形验证码的有效性，返回boolean值
        boolean verify = captcha.verify(code);
        if (verify) {
            return new ResponseEntity<>(200, "true", "true！");
        } else {
            return new ResponseEntity<>(500, "false", "false！");
        }
    }

    /**
     * 用户收藏(团游，景点，游记）
     */
    @GetMapping("/collect")
    public ResponseEntity<String> collectTravelNotes(@RequestBody Object collect){
        String collectJson = JSONObject.toJSONString(collect);
        //如果不包含userId
        if(Boolean.FALSE.equals(CommonUtils.isExistField("userId",collect))){
            return new ResponseEntity<>(500,"用户未登录");
        }
        //如果包含游记字段
        if (Boolean.TRUE.equals(CommonUtils.isExistField("notesId", collect))){
            int i = normalUserService.addTravelCollect(JSON.parseObject(collectJson, LgNormalUserTravelnotesCollect.class));
            if (i > 0) {
                return new ResponseEntity<>(200, "收藏游记成功！", "");
            } else {
                return new ResponseEntity<>(500, "收藏游记失败！", "");
            }
        }
        //如果包含团游字段
        if(Boolean.TRUE.equals(CommonUtils.isExistField("groupId", collect))){
            int i = normalUserService.addGroupCollect(JSON.parseObject(collectJson,LgNormalUserGroupCollect.class));
            if (i > 0) {
                return new ResponseEntity<>(200, "收藏团游成功！", "");
            } else {
                return new ResponseEntity<>(500, "收藏团游失败！", "");
            }
        }
        //如果包含景点
        if(Boolean.TRUE.equals(CommonUtils.isExistField("scenicId", collect))){
            int i = normalUserService.addScenicCollect(JSON.parseObject(collectJson,LgNormalUserScenicspotCollect.class));
            if (i > 0) {
                return new ResponseEntity<>(200, "收藏景点成功！", "");
            } else {
                return new ResponseEntity<>(500, "收藏景点失败！", "");
            }
        }
        return new ResponseEntity<>(500, "收藏失败！", "");
    }


    /**
     * 查询所有的收藏内容
     * 根据不同的分类 放入Redis的ZSet里面 统一设置  -key:"collect"  -value:"项目名字"  -score:时间
     */
    @GetMapping("/showAllCollects/{userId}")
    public ResponseEntity<List<UserCollects>> showAllCollects(@PathVariable("userId") int userId){
        List<UserCollects> list = normalUserService.showAllCollects(userId);
        if(!ObjectUtil.isNull(list)){
            return new ResponseEntity<>(200,"查询所有收藏成功",list);
        }else {
            return new ResponseEntity<>(500,"查询所有收藏失败");
        }
    }

}
