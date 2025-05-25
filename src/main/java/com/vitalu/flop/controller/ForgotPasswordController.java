package com.vitalu.flop.controller;

import java.time.Instant;
import java.util.Date;
import java.util.Objects;
import java.util.Random;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import com.vitalu.flop.auth.ChangePassword;
import com.vitalu.flop.auth.PasswordHasher;
import com.vitalu.flop.model.dto.MailBody;
import com.vitalu.flop.model.entity.ForgotPassword;
import com.vitalu.flop.model.entity.Usuario;
import com.vitalu.flop.model.repository.ForgotPasswordRepository;
import com.vitalu.flop.model.repository.UsuarioRepository;
import com.vitalu.flop.service.EmailService;

@RestController
@RequestMapping("/forgotPassword")
public class ForgotPasswordController {

	@Autowired
	private UsuarioRepository userRepository;

	@Autowired
	private EmailService emailService;

	@Autowired
	private ForgotPasswordRepository forgotPasswordRepository;

	@Autowired
	private PasswordHasher passwordEncoder;

	// enviar email para verificação por email
	@PostMapping("/verifyMail/{email}")
	public ResponseEntity<String> verifyEmail(@PathVariable String email) {
		Usuario user = userRepository.findByEmail(email)
				.orElseThrow(() -> new UsernameNotFoundException("Por favor forneça um email válido!"));

		int otp = otpGenerator();
		MailBody mailBody = MailBody.builder().to(email).text("Olá! Esse é seu código de verificação: " + otp)
				.subject("OTP for forgot password request").build();

		ForgotPassword fp = ForgotPassword.builder().otp(otp)
				// código válido por uma hora. 
				.expirationTime(new Date(System.currentTimeMillis() + 60 * 60 * 1000)).user(user).build();

		emailService.sendSimpleMessage(mailBody);
		forgotPasswordRepository.save(fp);

		return ResponseEntity.ok("Email sent for verification!");
	}

	@PostMapping("/verifyOtp/{otp}/{email}")
	public ResponseEntity<String> verifyOtp(@PathVariable Integer otp, @PathVariable String email) {
		Usuario user = userRepository.findByEmail(email)
				.orElseThrow(() -> new UsernameNotFoundException("Por favor forneca um email válido!"));

		ForgotPassword fp = forgotPasswordRepository.findByOtpAndUser(otp, user)
				.orElseThrow(() -> new UsernameNotFoundException("Código inválido para email: " + email));

		if (fp.getExpirationTime().before(Date.from(Instant.now()))) {
			forgotPasswordRepository.deleteById(fp.getFpid());
			return new ResponseEntity<>("OTP expirou!", HttpStatus.EXPECTATION_FAILED);
		}

		return ResponseEntity.ok("OTP verificado!");
	}

	@PostMapping("/changePassword/{email}")
	public ResponseEntity<String> changePasswordHandler(@RequestBody ChangePassword changePassword,
			@PathVariable String email) {

		if (!Objects.equals(changePassword.password(), changePassword.repeatPassword())) {

			return new ResponseEntity<>("Por favor digite a senha novamente!", HttpStatus.EXPECTATION_FAILED);
		}

		String encodedPassword = passwordEncoder.encode(changePassword.password());
		userRepository.updatePassword(email, encodedPassword);

		return ResponseEntity.ok("Senha foi atualizada!");

	}

	private Integer otpGenerator() {
		Random random = new Random();
		return random.nextInt(100_000, 999_999);
	}
}
