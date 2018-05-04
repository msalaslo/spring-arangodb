package com.msl.data.arangodb.got.runner;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;

import com.arangodb.ArangoCursor;
import com.msl.data.arangodb.got.entity.Character;
import com.msl.data.arangodb.got.entity.ChildOf;
import com.msl.data.arangodb.got.repository.CharacterRepository;

public class AQLRunner implements CommandLineRunner {
	
	private static final Logger logger = LoggerFactory.getLogger(AQLRunner.class.getName());


	@Autowired
	private CharacterRepository repository;

	@Override
	public void run(final String... args) throws Exception {
		logger.debug("# AQL queries");

		logger.debug("## Find all characters which are older than 21 (sort descending)");
		final Iterable<Character> older = repository.getOlderThan(21);
		older.forEach(item -> logger.debug(item.toString()));

		logger.debug("## Find all characters with surname 'Lannister' (sort by age ascending)");
		Iterable<Character> lannisters = repository.getWithSurname("Lannister");		
		lannisters.forEach(item -> logger.debug(item.toString()));

		logger.debug("## Find all characters with surname 'Lanister' which are older than 35");
		Map<String, Object> bindvars = new HashMap<>();
		bindvars.put("surname", "Lannister");
		bindvars.put("@col", Character.class);
		Iterable<Character> oldLannisters = repository.getWithSurnameOlderThan(35, bindvars);
		oldLannisters.forEach(System.out::println);
		oldLannisters.forEach(item -> logger.debug(item.toString()));
		
		logger.debug("## Find all characters with surname 'Lanister' which are older than 35 with Query Options");
		ArangoCursor<Character> oldLannistersCursor = repository.getWithSurnameOlderThanQueryOptions(35, bindvars);
		logger.debug(String.format("Found %s documents", oldLannistersCursor.getCount()));

		logger.debug("## Find all childs and grantchilds of 'Tywin Lannister' (sort by age descending)");
		repository.findByNameAndSurname("Tywin", "Lannister").ifPresent(tywin -> {
		Set<Character> childs = repository.getAllChildsAndGrandchilds(tywin.getId(), ChildOf.class);
		  childs.forEach(System.out::println);
		});
	}
}
