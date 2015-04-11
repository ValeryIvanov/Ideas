package com.walts.ideas.tests;

import android.test.AndroidTestCase;
import android.test.RenamingDelegatingContext;

import com.walts.ideas.db.Idea;
import com.walts.ideas.db.IdeasDbHelper;

import java.util.List;

public class DatabaseTest extends AndroidTestCase {

    private IdeasDbHelper dbHelper;

    private static final String TEST_FILE_PREFIX = "test_";

    //idea1
    final String TITLE1 = "First idea's title";
    final String DESC1 = "First idea's desc";
    Idea idea1 = new Idea(TITLE1, DESC1);

    //idea2
    final String TITLE2 = "Second idea's title";
    final String DESC2 = "Second idea's desc";
    Idea idea2 = new Idea(TITLE2, DESC2);

    @Override
    protected void setUp() throws Exception {
        super.setUp();

        RenamingDelegatingContext context = new RenamingDelegatingContext(getContext(), TEST_FILE_PREFIX);

        dbHelper = IdeasDbHelper.getInstance(context);
    }

    @Override
    protected void tearDown() throws Exception {
        super.tearDown();

        dbHelper.close();
    }

    public void test() {
        long id1 = dbHelper.insertIdea(idea1);
        long id2 = dbHelper.insertIdea(idea2);

        //--testing getting idea
        Idea ideaFromDB = dbHelper.getIdea(id1);

        assertEquals(TITLE1, ideaFromDB.title);
        assertEquals(DESC1, ideaFromDB.desc);

        assertTrue(!ideaFromDB.createdDate.isEmpty());

        //--testing getting all ideas
        List<Idea> ideas = dbHelper.getAllIdeas();

        assertEquals(2, ideas.size());
        assertEquals(TITLE2, ideas.get(1).title);
        assertEquals(DESC2, ideas.get(1).desc);

        //--testing updating idea
        final String NEW_TITLE1 = "First idea's new title";
        final String NEW_DESC1 = "First idea's new desc";

        ideaFromDB.title = NEW_TITLE1;
        ideaFromDB.desc = NEW_DESC1;

        int rowsAffected = dbHelper.updateIdea(ideaFromDB);

        assertEquals(1, rowsAffected);

        Idea updatedIdea = dbHelper.getIdea(id1);

        assertEquals(NEW_TITLE1, updatedIdea.title);
        assertEquals(NEW_DESC1, updatedIdea.desc);

        //--testing deleting idea
        dbHelper.deleteIdea(id1);

        assertEquals(null, dbHelper.getIdea(id1));

        //TODO password
    }

}
