package de.ufo.cinemasystem.services;

import java.math.BigDecimal;

import javax.money.CurrencyUnit;
import javax.money.Monetary;

import org.javamoney.moneta.Money;
import org.salespointframework.useraccount.Password;
import org.salespointframework.useraccount.Role;
import org.salespointframework.useraccount.UserAccount;
import org.salespointframework.useraccount.UserAccountManagement;
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



		public EmployeeService(EmployeeRepository employeeRepository, UserAccountManagement userAccountManagement, UserRepository userRepository) {
			this.employeeRepository = employeeRepository;
			this.userAccountManagement = userAccountManagement;
			this.userRepository = userRepository;
		}


		public short createEmployee(EmployeeRegistrationForm employeeRegistrationForm)
		{
			Assert.notNull(employeeRegistrationForm, "Registration form must not be null!");


			UserEntry     testUserEntry = new UserEntry();
			EmployeeEntry testEmployeeEntry = new EmployeeEntry();


			if (employeeRepository.findByJobMail(employeeRegistrationForm.getJobMail()).isPresent())
			{
				return 1;
			}

			short hoursPerWeek = Short.parseShort(employeeRegistrationForm.getHoursPerWeek());
			String salaryCleaned = employeeRegistrationForm.getSalary().replaceAll("[€,]", "");
			Long salaryLong = Long.parseLong(salaryCleaned);
			BigDecimal salaryAmount = BigDecimal.valueOf(salaryLong);
			CurrencyUnit currency = Monetary.getCurrency("EUR");
			Money salary = Money.of(salaryAmount, currency);

			if (!employeeRegistrationForm.getJobMail().endsWith("@ufo-kino.de") || employeeRepository.findByJobMail(employeeRegistrationForm.getJobMail()).isPresent()) {
				return 2;
			}

			if (hoursPerWeek > 50 || salary.isNegative() || salary.isZero() || salary.divide(hoursPerWeek * 4).isLessThan(Money.of(12, salary.getCurrency()))) {
				return 3;
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




	public void editEmployee(UserEntry.UserIdentifier id, String firstName, String lastName, String email, String job, String salary, String hours)
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
		if (salary != null && !salary.isEmpty()) {
			String salaryCleaned = salary.replaceAll("[€,]", "");
			Long salaryLong = Long.parseLong(salaryCleaned);
			BigDecimal salaryAmount = BigDecimal.valueOf(salaryLong);
			CurrencyUnit currency = Monetary.getCurrency("EUR");
			Money finishedSalary = Money.of(salaryAmount, currency);

			employeeEntry.setSalary(finishedSalary);
		}
		if (hours != null && !hours.isEmpty()) {
			employeeEntry.setHoursPerWeek(Short.parseShort(hours));
		}
		employeeRepository.save(employeeEntry);
	}
}

