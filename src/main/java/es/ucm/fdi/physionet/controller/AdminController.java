package es.ucm.fdi.physionet.controller;

import es.ucm.fdi.physionet.LocalData;
import es.ucm.fdi.physionet.controller.util.ControllerUtils;
import es.ucm.fdi.physionet.model.Absence;
import es.ucm.fdi.physionet.model.Appointment;
import es.ucm.fdi.physionet.model.User;
import es.ucm.fdi.physionet.model.enums.AbsenceStatus;
import es.ucm.fdi.physionet.model.enums.ServerMessages;
import es.ucm.fdi.physionet.model.enums.UserRole;
import es.ucm.fdi.physionet.model.util.Queries;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.util.FileCopyUtils;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.method.annotation.StreamingResponseBody;

import javax.persistence.EntityManager;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import javax.transaction.Transactional;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.List;

@Controller
@RequestMapping("/admin")
public class AdminController {

	private static final Logger log = LogManager.getLogger(PatientController.class);
	@Autowired
	private EntityManager entityManager;
	
	@Autowired
	private LocalData localData;
	
	@Autowired
	private PasswordEncoder passwordEncoder;

	@Autowired
	private ControllerUtils utils;

	@GetMapping("/patients")
	public String viewPatients(Model model) {
		log.debug("Hemos entrado a la ventana de admin vista pacientes");
		model.addAttribute("patients", entityManager.createNamedQuery("User.byRole").setParameter("role", "PATIENT")
				.getResultList());

		User sessionUser = utils.getFreshSessionUser();
		model.addAttribute("user", sessionUser);
		model.addAttribute("adminUserName", sessionUser.getName());
		model.addAttribute("role", UserRole.ADMIN.toString());
		return "admin-patient-view";
	}

	@GetMapping("/doctors")
	public String viewDoctors(Model model) {
		log.debug("Hemos entrado a la ventana de admin vista doctores");
		model.addAttribute("doctors",
				entityManager.createNamedQuery("User.byRole").setParameter("role", "DOCTOR")
						.getResultList());

		User sessionUser = utils.getFreshSessionUser();
		model.addAttribute("user", sessionUser);
		model.addAttribute("adminUserName", sessionUser.getName());
		model.addAttribute("role", UserRole.ADMIN.toString());
		return "admin-doctor-view";
	}

	@GetMapping("/userinfo")
	public String showUserInfo(Model model, @RequestParam long id) {
		User u = entityManager.find(User.class, id);
		model.addAttribute("selecteduser", u);
		if (u.hasRole(UserRole.PATIENT)) {
			model.addAttribute("appointments", utils.getFinalizedAppointments(u));
			return viewPatients(model);
		}
		else {
			return viewDoctors(model);
		}
	}
	@GetMapping("/createuserview")
	public String getViewCreateUser(Model model) {
		User sessionUser = utils.getFreshSessionUser();
		model.addAttribute("user", sessionUser);
		model.addAttribute("adminUserName", sessionUser.getName());
		model.addAttribute("role", UserRole.ADMIN.toString());
		return "admin-create-user";
	}
	@PostMapping("/createuser")
	@Transactional
	public String createUser(@RequestParam(value = "username") String username,
							 @RequestParam(value = "name") String name,
							 @RequestParam(value = "surname") String surname,
							 @RequestParam(value = "role") String role,
							 @RequestParam(value = "password") String password,
							 Model model){
		User newUser;
		List<User> target =  entityManager.createNamedQuery("User.byUsername", User.class).setParameter("username", username).getResultList();
		if(target.isEmpty()) {
			newUser = new User(username, role, name, surname);
			newUser.setPassword(passwordEncoder.encode(password));
			newUser.setEnabled((byte) 1);
			entityManager.persist(newUser);
			entityManager.flush();
			return showUserInfo(model, newUser.getId());
		}
		else {
			log.error("Ya hay un usuario con ese nombre");
			return getViewCreateUser(model);
		}


	}
	@PostMapping("/toggleuser")
	@Transactional
	public String toggleUser(Model model, @RequestParam long id) {
		User target = entityManager.find(User.class, id);
		if(target != null && target.getEnabled()==1) {
			target.setEnabled((byte) 0);
		}
		else {
			target.setEnabled((byte) 1);
		}
		return showUserInfo(model, id);
	}
	@PostMapping("/deleteuser")
	@Transactional
	public String deleteUser(Model model, @RequestParam long id) {
		User target = entityManager.find(User.class, id);
		if(target != null  && target.getEnabled()==0) {
			entityManager.remove(target);
		}
		if (target.hasRole(UserRole.PATIENT)) {
			return viewPatients(model);
		}
		else {
			return viewDoctors(model);
		}
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
		requester = entityManager.find(User.class, requester.getId());
		if (requester.getId() != target.getId() &&
				! requester.hasRole(UserRole.ADMIN)) {
			response.sendError(HttpServletResponse.SC_FORBIDDEN,
					"No eres administrador, y éste no es tu perfil");
		}

		if (edited.getPassword() != null && !edited.getPassword().equals("") && edited.getPassword().equals(pass2)) {
			// save encoded version of password
			target.setPassword(passwordEncoder.encode(edited.getPassword()));
		}

		target.setUsername(edited.getUsername());
		target.setName(edited.getName());
		target.setSurname(edited.getSurname());
		target.setFreeDaysLeft(edited.getFreeDaysLeft());
		if(!target.getRoles().equals(edited.getRoles())){
			target.setRoles(edited.getRoles());
			if (target.hasRole(UserRole.PATIENT)) {
				return viewDoctors(model);
			}
			else {
				return viewPatients(model);
			}
		}
		else return showUserInfo(model, id);
	}

	@PostMapping("/checkUser/{username}")
	@Transactional
	@ResponseBody // <-- "lo que devuelvo es la respuesta, tal cual"
	public String getUser(@PathVariable String username) {
		// Comprobamos que el usuario que busca es administrador
		User currentUser = utils.getFreshSessionUser();
		if(!currentUser.hasRole(UserRole.ADMIN)) {
			log.warn("User requesting information from username {} has no role of admin", username);
			log.warn("Current user information is: {}", currentUser.toString());
		}

		log.info("searching user with username: {}", username);
		String ret = "USED";
		List<User> target = entityManager.createNamedQuery("User.byUsername", User.class).setParameter("username", username).getResultList();
		if (target.isEmpty()){
			ret = "FREE";
		}
		return ret; // devuelve la cadena 'OK': gasta menos recursos
	}

	@GetMapping("/absences")
	public String getAbsences(Model model) {
		log.info("Attempting to get all absences");

		User sessionUser = utils.getFreshSessionUser();
		model.addAttribute("user", sessionUser);
		model.addAttribute("adminUserName", sessionUser.getName());
		model.addAttribute("role", UserRole.ADMIN.toString());
		model.addAttribute("absences", Absence.asTransferObjects(entityManager.createNamedQuery(Queries.GET_ALL_ABSENCES, Absence.class).getResultList()));

		return "admin-absences-view";
	}
	
	@GetMapping("/{id}/photo")
	public StreamingResponseBody getPhoto(@PathVariable long id, Model model) throws IOException {
		File f = localData.getFile("user", ""+id);
		InputStream in;
		if (f.exists()) {
			in = new BufferedInputStream(new FileInputStream(f));
		} else {
			in = new BufferedInputStream(getClass().getClassLoader()
					.getResourceAsStream("static/images/default-user-profile.png"));
		}
		return new StreamingResponseBody() {
			@Override
			public void writeTo(OutputStream os) throws IOException {
				FileCopyUtils.copy(in, os);
			}
		};
	}
	
	@PostMapping("/{id}/photo")
	public String postPhoto(
			HttpServletResponse response,
			@RequestParam("photo") MultipartFile photo,
			@PathVariable("id") String id, Model model, HttpSession session) throws IOException {
		User target = entityManager.find(User.class, Long.parseLong(id));
		model.addAttribute("user", target);
		
		// check permissions
		User requester = utils.getFreshSessionUser();
		if (requester.getId() != target.getId() &&
				! requester.hasRole(UserRole.ADMIN)) {
			response.sendError(HttpServletResponse.SC_FORBIDDEN, 
					"No eres administrador, y éste no es tu perfil");
			return showUserInfo(model, Long.parseLong(id));
		}
		
		log.info("Updating photo for user {}", id);
		File f = localData.getFile("user", id);
		if (photo.isEmpty()) {
			log.info("failed to upload photo: emtpy file?");
		} else {
			try (BufferedOutputStream stream =
					new BufferedOutputStream(new FileOutputStream(f))) {
				byte[] bytes = photo.getBytes();
				stream.write(bytes);
			} catch (Exception e) {
				log.warn("Error uploading " + id + " ", e);
			}
			log.info("Successfully uploaded photo for {} into {}!", id, f.getAbsolutePath());
		}
		return showUserInfo(model, Long.parseLong(id));
	}

	@PostMapping("/updateabcense")
	@Transactional
	public String updateAbcense(
			@RequestParam(required = true) long absenceid,
			@RequestParam(required = true) AbsenceStatus absencestatus,
			Model model) {

		Absence absence = entityManager.find(Absence.class, absenceid);

		if (absence != null) {
			absence.setStatus(absencestatus);
			model.addAttribute("successMessage", ServerMessages.ABSENCE_UPDATE_SUCCESS.getPropertyName());
		} else {
			model.addAttribute("errorMessage", ServerMessages.ABSENCE_UPDATE_ERROR.getPropertyName());
		}

		return getAbsences(model);

	}
}
