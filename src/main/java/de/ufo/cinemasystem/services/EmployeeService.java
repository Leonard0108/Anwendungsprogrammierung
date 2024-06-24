package de.ufo.cinemasystem.services;

import java.math.BigDecimal;
import java.util.Arrays;
import java.util.List;

import javax.money.CurrencyUnit;
import javax.money.Monetary;
import javax.money.MonetaryException;

import org.javamoney.moneta.Money;
import org.salespointframework.useraccount.Password;
import org.salespointframework.useraccount.Role;
import org.salespointframework.useraccount.UserAccount;
import org.salespointframework.useraccount.UserAccountManagement;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;
import org.springframework.util.Assert;

import de.ufo.cinemasystem.additionalfiles.EmployeeRegistrationForm;
import de.ufo.cinemasystem.models.EmployeeEntry;
import de.ufo.cinemasystem.models.UserEntry;
import de.ufo.cinemasystem.repository.EmployeeRepository;
import de.ufo.cinemasystem.repository.UserRepository;



@Service
public class EmployeeService {
		EmployeeRepository employeeRepository;
		UserAccountManagement userAccountManagement;
		UserRepository userRepository;
		private static final List<String> KNOWN_EMAIL_PROVIDERS = Arrays.asList(
		"gmail.com", "yahoo.com", "outlook.com", "hotmail.com", "aol.com",
		"icloud.com", "mail.com", "gmx.com", "yandex.com", "protonmail.com"
		);

	public EmployeeService(EmployeeRepository employeeRepository, UserAccountManagement userAccountManagement, UserRepository userRepository) {
		this.employeeRepository = employeeRepository;
		this.userAccountManagement = userAccountManagement;
		this.userRepository = userRepository;
	}

	private boolean isKnownEmailProvider(String email) {
		if (email != null && email.contains("@") && !email.endsWith("@"))
		{
			if (KNOWN_EMAIL_PROVIDERS.stream().anyMatch(email::endsWith)) {
				return true;
			}

			String host = email.substring(email.lastIndexOf("@") + 1);

			try {
				java.net.InetAddress.getByName(host);
				return true;
			}
			catch (java.net.UnknownHostException e) {
				return false;
			}
		}
		return false;
	}




	public short createEmployee(EmployeeRegistrationForm employeeRegistrationForm)
	{
		Assert.notNull(employeeRegistrationForm, "Registration form must not be null!");

		try {
			if (!isKnownEmailProvider(employeeRegistrationForm.getEMail())) {
				return 4;  // Return a new code for unknown email providers
			}


			if (employeeRepository.findByJobMail(employeeRegistrationForm.getJobMail()).isPresent()) {
				return 2;
			}

			short hoursPerWeek = Short.parseShort(employeeRegistrationForm.getHoursPerWeek());
			String salaryCleaned = employeeRegistrationForm.getSalary().replaceAll("[€,]", "");
			long salaryLong = Long.parseLong(salaryCleaned);
			BigDecimal salaryAmount = BigDecimal.valueOf(salaryLong);
			CurrencyUnit currency = Monetary.getCurrency("EUR");
			Money salary = Money.of(salaryAmount, currency);

			if (!employeeRegistrationForm.getJobMail().endsWith("@ufo-kino.de") || employeeRepository.findByJobMail(employeeRegistrationForm.getJobMail()).isPresent()) {
				return 3;
			}

			if (hoursPerWeek > 50 || salary.isNegative() || salary.isZero()) {
				return 5;
			}

			if (salary.divide((hoursPerWeek << 2)).isLessThan(Money.of(12, salary.getCurrency())))
			{
				return 6;
			}


			UserEntry userEntry = userRepository.findByeMail(employeeRegistrationForm.getEMail());

			if (userEntry == null /*&& userRepository.findByUserAccountEmail(employeeRegistrationForm.getEMail()) == null*/) {
				UserAccount userAccount = userAccountManagement.create(employeeRegistrationForm.getUsername(), Password.UnencryptedPassword.of(employeeRegistrationForm.getPassword()), Role.of("EMPLOYEE"));
				userAccount.setFirstname(employeeRegistrationForm.getFirstName());
				userAccount.setLastname(employeeRegistrationForm.getLastName());
				userAccount.setEmail(employeeRegistrationForm.getEMail());

				userEntry = new UserEntry(userAccount, employeeRegistrationForm.getFirstName(), employeeRegistrationForm.getLastName(), employeeRegistrationForm.getEMail(), employeeRegistrationForm.getStreetName(), employeeRegistrationForm.getHouseNumber(), employeeRegistrationForm.getCity(), employeeRegistrationForm.getPostalCode(), employeeRegistrationForm.getState(), employeeRegistrationForm.getCountry());

				userRepository.save(userEntry);
			}

			EmployeeEntry employeeEntry = new EmployeeEntry(userEntry, salary, employeeRegistrationForm.getJobMail(), hoursPerWeek);
			employeeRepository.save(employeeEntry);

			return 0;
		}
		catch (NumberFormatException e) {
			return 7;  // Code for number format exceptions
		}
		catch (MonetaryException e) {
			return 8;  // Code for monetary exceptions
		}
		catch (DataIntegrityViolationException e) {
			return 9;  // Code for data integrity violations
		}
		catch (Exception e)
		{
			return 1;
		}
	}
 

	/**
	 *
	 * @param id id of the to edit employee account
	 * @param firstName first name of the  employee. Changeable because of gender diversity.
	 * @param lastName last name of the  employee. Changeable because of marriage.
	 * @param email maybe the employee changes him/Z her main e-mail-address
	 * @param job changes the role of the employee
	 * @param salary in- or decrease the salary
	 * @param hours changes the number of hours
	 * @return 0 if everything works out, 1 if there are negative hours, 2 if to many hours and two if the salary is too bad.
	 */
	public int editEmployee(UserEntry.UserIdentifier id, String firstName, String lastName, String email, String job, String salary, String hours)
	{
		UserEntry     userEntry     = userRepository.findById(id).orElseThrow();
		EmployeeEntry employeeEntry = employeeRepository.findById(id).orElseThrow();

		if (firstName != null && !firstName.isEmpty()) {
			userEntry.setFirstName(firstName);
		}
		if (lastName != null && !lastName.isEmpty()) {
			userEntry.setLastName(lastName);
		}
		if (email != null && !email.isEmpty()) {
			userEntry.setEMail(email);
		}
		if (job.contains("EMPLOYEE") && !job.contains("AUTHORIZED_EMPLOYEE"))
		{
			userEntry.getUserAccount().add(Role.of("EMPLOYEE"));
			userEntry.getUserAccount().remove(Role.of("AUTHORIZED_EMPLOYEE"));
		}
		if (job.contains("AUTHORIZED_EMPLOYEE"))
		{
			userEntry.getUserAccount().add(Role.of("AUTHORIZED_EMPLOYEE"));
			userEntry.getUserAccount().remove(Role.of("EMPLOYEE"));
		}


		if (hours != null && !hours.isEmpty()) {
			try {
				short hoursPerWeek = Short.parseShort(hours);
				if (hoursPerWeek < 1) {
					return 1;
				}

				if (hoursPerWeek > 50)
				{
					return 2;
				}

				employeeEntry.setHoursPerWeek(hoursPerWeek);
			}
			catch (Exception e)
			{
				return 4;
			}
		}


		if (salary != null && !salary.isEmpty()) {
			String salaryCleaned = salary.replaceAll("[€,]", "");
			try {
				long salaryLong = Long.parseLong(salaryCleaned);

				if (salaryLong < 1 || (salaryLong / employeeEntry.getHoursPerWeek() < 12))
				{
					return 3;
				}


				BigDecimal salaryAmount = BigDecimal.valueOf(salaryLong);
				CurrencyUnit currency = Monetary.getCurrency("EUR");
				Money finishedSalary = Money.of(salaryAmount, currency);

				employeeEntry.setSalary(finishedSalary);

			}
			catch (Exception e)
			{
				return 5;
			}
		}

		employeeRepository.save(employeeEntry);

		return 0;
	}
}