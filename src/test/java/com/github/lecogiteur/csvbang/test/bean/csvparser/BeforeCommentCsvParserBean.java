package com.github.lecogiteur.csvbang.test.bean.csvparser;

import com.github.lecogiteur.csvbang.annotation.CsvComment;
import com.github.lecogiteur.csvbang.annotation.CsvComment.DIRECTION;
import com.github.lecogiteur.csvbang.annotation.CsvField;
import com.github.lecogiteur.csvbang.annotation.CsvType;

@CsvType
public class BeforeCommentCsvParserBean {
	
	@CsvComment(direction=DIRECTION.BEFORE_RECORD)
	public Integer myComment;
	
	@CsvField
	public String field1;

}
