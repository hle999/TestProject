package com.sen.test.provider;


/**
 * Editor: sgc
 * Date: 2015/04/06
 */
public class TestTable1 {

    public static final String TABLE_NAME = "text_table1";
    public static final String ID = "_id";
    public static final String NAME = "name";
    public static final String MESSAGE = "msg";

    public static final String COLUMNS[] = {ID, NAME, MESSAGE};

    public static final String CREATE_TABLE_COMMAND = "CREATE TABLE "+TABLE_NAME+" ( "+
                                                        ID+" INTEGER PRIMARY KEY, "+
                                                        NAME+" TEXT, "+
                                                        MESSAGE+" TEXT)";

}
