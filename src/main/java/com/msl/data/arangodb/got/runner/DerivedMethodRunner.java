package com.msl.data.arangodb.got.runner;

import java.util.Collection;
import java.util.List;
import java.util.Optional;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.ComponentScan;

import com.msl.data.arangodb.got.entity.Character;
import com.msl.data.arangodb.got.repository.CharacterRepository;

@ComponentScan("com.arangodb.spring.demo")
public class DerivedMethodRunner implements CommandLineRunner {
	
	private static final Logger logger = LoggerFactory.getLogger(DerivedMethodRunner.class.getName());


	@Autowired
	private CharacterRepository repository;

	@Override
	public void run(final String... args) throws Exception {
		logger.debug("# Derived queries");

		logger.debug("## Find all characters with surname 'Lannister'");
		Iterable<Character> lannisters = repository.findBySurname("Lannister");
		lannisters.forEach(item -> logger.debug(item.toString()));


		logger.debug("## Find top 2 Lannnisters ordered by age");
		Collection<Character> top2 = repository.findTop2DistinctBySurnameIgnoreCaseOrderByAgeDesc("lannister");
		top2.forEach(item -> logger.debug(item.toString()));

		logger.debug(
				"## Find all characters which name is 'Bran' or 'Sansa' and it's surname ends with 'ark' and are between 10 and 16 years old");
		List<Character> youngStarks = repository.findBySurnameEndsWithAndAgeBetweenAndNameInAllIgnoreCase("ark", 10, 16,
				new String[] { "Bran", "Sansa" });
		youngStarks.forEach(item -> logger.debug(item.toString()));

		logger.debug("## Find a single character by name & surname");
		Optional<Character> tyrion = repository.findByNameAndSurname("Tyrion", "Lannister");
		tyrion.ifPresent(c -> {
			logger.debug(String.format("Found %s", c));
		});
		
		logger.debug("## Count how many characters are still alive");
		Integer alive = repository.countByAliveTrue();
		logger.debug(String.format("There are %s characters still alive", alive));
		
//		logger.debug("## Remove all characters except of which surname is 'Stark' and which are still alive");
//		repository.removeBySurnameNotLikeOrAliveFalse("Stark");
//		repository.findAll().forEach(System.out::println);
	}

}