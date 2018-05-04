package com.msl.data.arangodb.got.runner;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.data.domain.Example;
import org.springframework.data.domain.ExampleMatcher;

import com.msl.data.arangodb.got.entity.Character;
import com.msl.data.arangodb.got.repository.CharacterRepository;

@ComponentScan("com.msl.data.arangodb.got")
public class ByExampleRunner implements CommandLineRunner {
	
	private static final Logger logger = LoggerFactory.getLogger(ByExampleRunner.class.getName());

	@Autowired
	private CharacterRepository repository;

	final Character nedStark = new Character("Ned", "Stark", false, 41);

	@Override
	public void run(final String... args) throws Exception {
		logger.debug("# Query by example");
		logger.debug(String.format("## Find character which exactly match %s", nedStark));
		Character foundNedStark = repository.findOne(Example.of(nedStark));
		logger.debug(String.format("Found %s", foundNedStark));

		logger.debug("## Find all dead Starks");
		Iterable<Character> allDeadStarks = repository
				.findAll(Example.of(new Character(null, "Stark", false), ExampleMatcher.matchingAll()
						.withMatcher("surname", match -> match.exact()).withIgnorePaths("name", "age")));
		allDeadStarks.forEach(item -> logger.debug(item.toString()));


		logger.debug("## Find all Starks which are 30 years younger than Ned Stark");

		Iterable<Character> allYoungerStarks = repository.findAll(
				Example.of(foundNedStark, ExampleMatcher.matchingAll().withMatcher("surname", match -> match.exact())
						.withIgnorePaths("id", "name", "alive").withTransformer("age", age -> ((int) age) - 30)));
		allYoungerStarks.forEach(item -> logger.debug(item.toString()));


		logger.debug("## Find all character which surname ends with 'ark' (ignore case)");
		Iterable<Character> ark = repository.findAll(Example.of(new Character(null, "ark", false),
				ExampleMatcher.matchingAll().withMatcher("surname", match -> match.endsWith()).withIgnoreCase()
						.withIgnorePaths("name", "alive", "age")));
		ark.forEach(item -> logger.debug(item.toString()));


	}

}
