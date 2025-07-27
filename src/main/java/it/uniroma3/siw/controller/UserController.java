package it.uniroma3.siw.controller;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.logout.SecurityContextLogoutHandler;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import it.uniroma3.siw.model.Credentials;
import it.uniroma3.siw.model.User;
import it.uniroma3.siw.service.AuthService;
import it.uniroma3.siw.service.CredentialsService;
import it.uniroma3.siw.service.UserService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;

@Controller
public class UserController {

	@Autowired
	private UserService userService;
	@Autowired
	private CredentialsService credentialsService;
	@Autowired
	private AuthService authService;

	@GetMapping("/profile/manage")
	public String manageProfile(Model model, @RequestParam(required = false) String success) {
		Credentials creds = authService.getCurrentUserCredentials();
		if (creds == null) {
			return "redirect:/login";
		}

		model.addAttribute("user", creds.getUser());
		model.addAttribute("credentials", creds);

		// Gestione messaggi di successo
		if ("true".equals(success)) {
			model.addAttribute("profileSuccess", "Profile updated successfully");
		}

		return "formProfile.html";
	}


	@PostMapping("/profile")
	public String submitProfile(@Valid @ModelAttribute("user") User user, BindingResult userBr, Model model) {

		if (userBr.hasErrors()) {
			Credentials creds = authService.getCurrentUserCredentials();
			model.addAttribute("credentials", creds);
			return "formProfile.html";
		}

		// Ricavo le credenziali dell'User corrente
		Credentials currentUserCred = authService.getCurrentUserCredentials();
		if (currentUserCred == null) {
			return "redirect:/login";
		}

		// Aggiorno solo i campi permessi 
		currentUserCred.getUser().setName(user.getName());
		currentUserCred.getUser().setSurname(user.getSurname());
		currentUserCred.getUser().setEmail(user.getEmail());

		userService.saveUser(currentUserCred.getUser());

		return "redirect:/profile/manage?success=true";
	}


	@PostMapping("/profile/credentials")
	public String changeCredentials(@RequestParam(value = "newUsername", required = false) String newUsername,
			@RequestParam(value = "newPassword", required = false) String newPassword,
			@RequestParam(value = "confirmNewPassword", required = false) String confirmNewPassword, Model model,
			RedirectAttributes redirectAttributes, HttpServletRequest request, HttpServletResponse response) {

		// Ottieni le credenziali correnti
		Credentials currentCredentials = authService.getCurrentUserCredentials();
		if (currentCredentials == null) {
			return "redirect:/login";
		}

		boolean hasChanges = false;
		boolean hasErrors = false;

		// Validazione Username
		if (newUsername != null && !newUsername.trim().isEmpty()) {
			newUsername = newUsername.trim();

			// Controllo se l'username è diverso dall'attuale
			if (!newUsername.equals(currentCredentials.getUsername())) {
				// Controllo se l'username è già in uso
				if (!credentialsService.isUsernameAvailable(newUsername, currentCredentials.getId())) {
					model.addAttribute("usernameError", "Username already exists");
					hasErrors = true;
				} else {
					hasChanges = true;
				}
			}
		}

		// Validazione Password
		if (newPassword != null && !newPassword.trim().isEmpty()) {
			if (confirmNewPassword == null || !newPassword.equals(confirmNewPassword)) {
				model.addAttribute("passwordError", "Passwords do not match");
				hasErrors = true;
			} else {
				hasChanges = true;
			}
		}

		// Se ci sono errori, vado alla pagina del profilo
		if (hasErrors) {
			model.addAttribute("user", currentCredentials.getUser());
			model.addAttribute("credentials", currentCredentials);
			return "formProfile.html";
		}

		// Se non ci sono cambiamenti, vado alla pagina del profilo
		if (!hasChanges) {
			model.addAttribute("noChanges", "No changes detected");
			model.addAttribute("user", currentCredentials.getUser());
			model.addAttribute("credentials", currentCredentials);
			return "formProfile.html";
		}

		// Applica i cambiamenti
		try {
			credentialsService.updateCredentials(currentCredentials, newUsername, newPassword);

			// Logout automatico dopo il cambio credenziali
			new SecurityContextLogoutHandler().logout(request, response,
					SecurityContextHolder.getContext().getAuthentication());

			// Messaggio di successo
			redirectAttributes.addFlashAttribute("credentialsChanged", true);
			return "redirect:/login?credentials=changed";

		} catch (Exception e) {
			model.addAttribute("updateError", "Error updating credentials: " + e.getMessage());
			model.addAttribute("user", currentCredentials.getUser());
			model.addAttribute("credentials", currentCredentials);
			return "formProfile.html";
		}
	}
}
