package pro.jsoft.demand.persistence.repositories.demo;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Repository;

import lombok.val;
import lombok.extern.slf4j.Slf4j;
import pro.jsoft.demand.persistence.model.Field;
import pro.jsoft.demand.persistence.model.Form;
import pro.jsoft.demand.persistence.repositories.FormRepository;

@Repository
@Profile("demo")
@Slf4j
public class FormRepositoryDemoImpl implements FormRepository {
	private List<Form> formList = new ArrayList<>();
	
	private static final String[] JOBS = {
			"Вызов уборщицы", 
			"Замена потолочных ламп", 
			"Отсутствие бумажных полотенец в санузлах", 
			"Ремонт сантехники в санузлах", 
			"Перемещение рабочих мест", 
			"Перемещение отдельных ТМЦ", 
			"Монтаж предметов интерьера и оборудования",
			"Монтаж кабинетных табличек",
			"Ремонт мебели",
			"Ремонт дверных замков",
			"Ремонт жалюзи, карнизов",
			"Ремонт сейфов и бухгалтерских шкафов",
			"Ремонт бытовой техники"
	};
	
	public FormRepositoryDemoImpl() {
		// Janitary and utils
		
		for (String title : JOBS) {
			val form = new Form("form" + formList.size(), "TEST", title, Collections.emptySet());
			formList.add(form);
		}
		/*
		val field11 = new Field("field11", "Вид услуги", "select", "s1=Вызов уборщицы;s2=Замена потолочных ламп;s3=Отсутствие бумажных полотенец в санузлах;s4=Ремонт сантехники в санузлах;s5=Перемещение рабочих мест;s6=Перемещение отдельных ТМЦ;s7=Монтаж предметов интерьера и оборудования;s8=Монтаж кабинетных табличек;s9=Ремонт мебели;s10=Ремонт дверных замков;s11=Ремонт жалюзи, карнизов;s12=Ремонт сейфов и бухгалтерских шкафов;s13=Ремонт бытовой техники");
		val fieldSet1 = new LinkedHashSet<>(Arrays.asList(field11));
		val form1 = new Form("form1", "TEST", "Заявка 1", fieldSet1);
		formList.add(form1);
		*/
		// Printing

		val field21 = new Field("field21", "Название", "text", null);
		val field22 = new Field("field22", "Размер бумаги", "select", "s1=A4;s2=A3;s3=A1");
		val field23 = new Field("field23", "Тип печати", "select", "c1=Черно-белая;c2=Цветная");
		val field24 = new Field("field24", "Количество страниц", "number", null);
		val field25 = new Field("field25", "Количество экземпляров", "number", null);
		val field26 = new Field("field26", "Исходный документ", "select", "t1=Загрузка из файла;t2=Печатный вариант");

		val fieldSet2 = new LinkedHashSet<>(Arrays.asList(field21, field22, field23, field24, field25, field26));
		val form2 = new Form("form" + formList.size(), "TEST2", "Тиражирование документов", fieldSet2);
		formList.add(form2);
		
		log.debug("Using demo FormRepository");
	}

	@Override
	public List<Form> findByProfileOrderByName(final String profile) {
		return formList.stream().filter(f -> profile.equalsIgnoreCase(f.getProfile())).collect(Collectors.toList());
	}

	@Override
	public Iterable<Form> findAll() {
		return formList;
	}

	@Override
	public long count() {
		return formList.size();
	}


	@Override
	public <S extends Form> S save(S entity) {
		throw new UnsupportedOperationException();
	}

	@Override
	public <S extends Form> Iterable<S> saveAll(Iterable<S> entities) {
		throw new UnsupportedOperationException();
	}

	@Override
	public Optional<Form> findById(String id) {
		throw new UnsupportedOperationException();
	}

	@Override
	public boolean existsById(String id) {
		return false;
	}

	@Override
	public Iterable<Form> findAllById(Iterable<String> ids) {
		throw new UnsupportedOperationException();
	}

	@Override
	public void deleteById(String id) {
		throw new UnsupportedOperationException();
	}

	@Override
	public void delete(Form entity) {
		throw new UnsupportedOperationException();
	}

	@Override
	public void deleteAll(Iterable<? extends Form> entities) {
		throw new UnsupportedOperationException();
	}

	@Override
	public void deleteAll() {
		throw new UnsupportedOperationException();
	}
}
