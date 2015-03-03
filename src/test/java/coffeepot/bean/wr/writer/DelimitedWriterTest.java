/*
 * Copyright 2013 - Jeandeson O. Merelis
 */
package coffeepot.bean.wr.writer;

/*
 * #%L
 * coffeepot-bean-wr
 * %%
 * Copyright (C) 2013 Jeandeson O. Merelis
 * %%
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 *      http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 * #L%
 */
import coffeepot.bean.wr.model.Child;
import coffeepot.bean.wr.model.Job;
import coffeepot.bean.wr.model.Person;
import coffeepot.bean.wr.typeHandler.TypeHandlerFactory;
import coffeepot.bean.wr.writer.customHandler.LowStringHandler;
import coffeepot.bean.wr.writer.customHandler.DateTimeHandler;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.Writer;
import java.util.Date;
import java.util.LinkedList;
import java.util.List;
import org.joda.time.DateTime;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Assert;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

/**
 *
 * @author Jeandeson O. Merelis
 */
public class DelimitedWriterTest {

    public DelimitedWriterTest() {
    }

    @BeforeClass
    public static void setUpClass() {
    }

    @AfterClass
    public static void tearDownClass() {
    }

    @Before
    public void setUp() {
    }

    @After
    public void tearDown() {
    }
    
    //TODO: MORE TESTS

    @Test
    public void testWrite() throws Exception {

        File file = new File("TESTE_.TXT");
        Writer w = new FileWriter(file);

        DelimitedWriter instance = new DelimitedWriter(w);
        instance.setDelimiter('|');
        instance.setEscape('\\');
        instance.setRecordInitializator("");
        instance.setRecordTerminator("|\r\n");

        //set new custom TypeHandler as default for a class
        TypeHandlerFactory handlerFactory = instance.getObjectParserFactory().getHandlerFactory();
        handlerFactory.registerTypeHandlerClassFor(DateTime.class, DateTimeHandler.class);

        //set new custom TypeHandler as default for the class String
        handlerFactory.registerTypeHandlerClassFor(String.class, LowStringHandler.class);

        //set new custom TypeHandler as default for Enum
        handlerFactory.registerTypeHandlerClassFor(Enum.class, Person.EncodedEnumHandler.class);

        //instance.createParser(Person.class);
        Person obj = new Person();
        obj.setName("Jean");
        obj.setAge(37);        
        obj.setLongNumber(Long.MIN_VALUE);
        obj.setBirthday(new Date());
        obj.setJodaDateTime(DateTime.now());
        obj.setSalary(5999.9);
        obj.setGender(Person.Gender.MALE);

        instance.write(obj);

        obj = new Person();
        obj.setName("John");
        obj.setAge(14);
        instance.write(obj);

        obj = new Person();
        obj.setName("Ana");
        obj.setAge(11);
        instance.write(obj);

        obj = new Person();
        obj.setName("Jean");
        obj.setAge(37);

        List<Child> chidren = new LinkedList<>();

        Child child = new Child();
        child.setName("John");
        child.setAge(14);
        chidren.add(child);

        child = new Child();
        child.setName("Ana");
        child.setAge(11);
        chidren.add(child);

        obj.setChildren(chidren);

        List<Job> jobs = new LinkedList<>();

        jobs.add(new Job("initial\\First Job", "11", "22", "33"));
        jobs.add(new Job("Second job|escape test", "44", "55", "66"));
        jobs.add(new Job("3th job", "77", "88", "99"));
        jobs.add(new Job("4th job", "00", "aa", "bb"));

        obj.setJobs(jobs);
        instance.write(obj);

//        instance.clearParsers();
//        instance.write(obj, "testGroupRecord");
        w.flush();
        w.close();

        FileReader in = new FileReader(file);
        try (BufferedReader reader = new BufferedReader(in)) {
            String line;

            line = reader.readLine();
            Assert.assertEquals("PERSON|jean|5|03/03/2015|0|", line);

            line = reader.readLine();
            Assert.assertEquals("PERSON|john|5||0|", line);

            line = reader.readLine();
            Assert.assertEquals("PERSON|ana|5||0|", line);

            line = reader.readLine();
            Assert.assertEquals("PERSON|jean|5||2|initial\\\\first job|33|second job\\|escape test|66|3th job|99|4th job|bb|", line);

            line = reader.readLine();
            Assert.assertEquals("initial\\\\first job|33|", line);

            line = reader.readLine();
            Assert.assertEquals("second job\\|escape test|66|", line);

            line = reader.readLine();
            Assert.assertEquals("3th job|99|", line);

            line = reader.readLine();
            Assert.assertEquals("4th job|bb|", line);

            line = reader.readLine();
            Assert.assertNull(line);

        }

    }
}
