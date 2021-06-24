package com.egao.common.test.controller;

import com.egao.common.core.web.JsonResult;
import com.egao.common.system.entity.User;
import com.egao.common.system.service.UserService;
import org.apache.shiro.SecurityUtils;
import org.apache.shiro.subject.Subject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import com.egao.common.core.web.BaseController;
import com.egao.common.test.service.MenurService;
import org.springframework.web.bind.annotation.ResponseBody;

@Controller
@RequestMapping("/users")
public class UserrController extends BaseController{
	
	@Autowired
	private MenurService menurService;
	@Autowired
	private UserService userTwoService;
	
	@RequestMapping("/index")
	public String index(Model model) {
		Subject subject = SecurityUtils.getSubject();
        Object object = subject.getPrincipal();
        if(object != null) {
        	model.addAttribute("user", (User)object);
    		model.addAttribute("menu", menurService.list());
    		return "/user/index.html";
        }else {
        	User user2 = new User();
			user2.setUsername("游客");
			user2.setUserId(-1);
			model.addAttribute("user", user2);
			model.addAttribute("menu", menurService.list());
			return "default/login.html";
        }
		
	}
	
	@RequestMapping("/order")
	public String order(Model model) {
		Subject subject = SecurityUtils.getSubject();
        Object object = subject.getPrincipal();
		if(object != null) {
			model.addAttribute("user", (User)object);
    		model.addAttribute("menu", menurService.list());
    		return "/user/order.html";
		}else {
			User user2 = new User();
			user2.setUsername("游客");
			user2.setUserId(-1);
			model.addAttribute("user", user2);
			model.addAttribute("menu", menurService.list());
			return "default/login.html";
		}
	}

	@ResponseBody
	@PostMapping("/repass")
	public JsonResult repass(String username, String pass,String repass) {
		User userTwo = new User();
		userTwo.setUserId(userTwoService.getByUsername(username).getUserId());
		if(!pass.equals(repass)){
			return JsonResult.error("两次密码输入不一值");
		}else {
			userTwo.setPassword(userTwoService.encodePsw(pass));
			userTwoService.updateById(userTwo);
		}
		return JsonResult.ok("修改成功");
	}


	

}
