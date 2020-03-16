package es.ucm.fdi.physionet.controller;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import es.ucm.fdi.physionet.model.User;
import es.ucm.fdi.physionet.model.enums.UserRole;

import java.io.IOException;
import java.util.List;

import javax.persistence.EntityManager;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import javax.transaction.Transactional;

@Controller
@RequestMapping("/admin")
public class AdminController {

	private static Logger log = LogManager.getLogger(PatientController.class);
	@Autowired 
	private EntityManager entityManager;

    public AdminController(EntityManager entityManager) {
        this.entityManager = entityManager;
    }
    
	@Autowired
	private PasswordEncoder passwordEncoder;

    @GetMapping("")
	public String index(Model model) {
    	log.debug("Hemos entrado a la ventana de admin");
		model.addAttribute("patients", entityManager.createNamedQuery("User.byRole").setParameter("role", "PATIENT")
				.getResultList());
		
		return "admin-patient-view";
	}
    
    @GetMapping("/userinfo")
    public String showUserInfo(Model model, @RequestParam long id) {
    	model.addAttribute("selecteduser", entityManager.find(User.class, id)); 	
    	return index(model);
    }
    @GetMapping("/createuserview")
    public String getViewCreateUser() {
    	return "admin-create-user";
    }
    @PostMapping("/createuser")
    @Transactional
    public String createUser(HttpServletResponse response, 
    		@RequestParam (value = "username", required = true) String username,
    		@RequestParam (value = "name", required = true) String name, 
    		@RequestParam (value = "surname", required = true) String surname,
    		@RequestParam (value = "role", required = true) String role,
    		@RequestParam (value = "password", required = true) String password,
			Model model, HttpSession session ){
    		User newUser = null;
    		List<User> target =  entityManager.createNamedQuery("User.byUsername").setParameter("username", username).getResultList();
    		if(target.isEmpty()) {
    			newUser= new User(username, role, name, surname);	
    			newUser.setPassword(passwordEncoder.encode(password));
    			newUser.setEnabled((byte) 1);
    			entityManager.persist(newUser);
    			entityManager.flush();
    			return showUserInfo(model, newUser.getId());
    		}
    		else {
    			log.error("Ya hay un usuario con ese nombre");
    			return getViewCreateUser();
    		}
			
    	
    }
    @PostMapping("/toggleuser")
    @Transactional
    public String toggleUser(Model model,	@RequestParam long id) {
    	User target = entityManager.find(User.class, id);
    	if(target != null && target.getEnabled()==1) {
    		target.setEnabled((byte) 0);
    	}
    	else {
    		target.setEnabled((byte)1);
    	}
    	return showUserInfo(model, id);
    }
    @PostMapping("/deleteuser")
    @Transactional
    public String deleteUser(Model model,	@RequestParam long id) {
    	User target = entityManager.find(User.class, id);
    	if(target != null  && target.getEnabled()==0) {
    		entityManager.remove(target);
    	}
		return index(model);
    	
    }
    
    @PostMapping("/updateuser{id}")
	@Transactional
	public String postUser(
			HttpServletResponse response,
			@PathVariable (name="id") long id, 
			@ModelAttribute User edited, 
			@RequestParam(required=false) String pass2,
			Model model, HttpSession session) throws IOException {
		User target = entityManager.find(User.class, id);	
		User requester = (User)session.getAttribute("u");
		if (requester.getId() != target.getId() &&
				! requester.hasRole(UserRole.ADMIN)) {			
			response.sendError(HttpServletResponse.SC_FORBIDDEN, 
					"No eres administrador, y éste no es tu perfil");
		}
		
		if (pass2 != null && edited.getPassword() != null && edited.getPassword().equals(pass2)) {
			// save encoded version of password
			target.setPassword(passwordEncoder.encode(edited.getPassword()));
		}		
		target.setUsername(edited.getUsername());
		target.setName(edited.getName());
		target.setSurname(edited.getSurname());
		if(!target.getRoles().equals(edited.getRoles())){
			target.setRoles(edited.getRoles());
			return index(model);
		}
		else return showUserInfo(model, id);
	}
}
