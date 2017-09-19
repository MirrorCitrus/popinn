package cdf.com.easypop;

import org.junit.Test;

import static org.junit.Assert.*;

/**
 * Example local unit test, which will execute on the development machine (host).
 *
 * @see <a href="http://d.android.com/tools/testing">Testing documentation</a>
 */
public class ExampleUnitTest {

    private String getDataBindingClassName(String layoutName) {
        String tag = ":layout/";
        int index = layoutName == null ? -1 : layoutName.indexOf(tag);
        if (index >= 0) {
            index += tag.length();
            if (index < layoutName.length()) {
                layoutName = layoutName.substring(index);

                String[] segments = layoutName.split("_");
                String seg;
                for (int i = 0; i < segments.length; i++) {
                    seg = segments[i];
                    seg = seg.toLowerCase();
                    if (seg.length() > 0) {
                        seg = Character.toUpperCase(seg.charAt(0)) + seg.substring(1);
                    }
                    segments[i] = seg;
                }

                StringBuilder sb = new StringBuilder();
                for (String segment : segments) {
                    sb.append(segment);
                }
                sb.append("Binding");
                return sb.toString();
            }
        }
        return null;
    }
    
    @Test
    public void addition_isCorrect() throws Exception {
        assertEquals(4, 2 + 2);
    }
    
    @Test
    public void testLayoutName2DataBindingName() {
        assertEquals("PopUserBinding", getDataBindingClassName("cdf.com.easypop:layout/pop_user"));
        assertEquals("PopUserA3TesuBinding", getDataBindingClassName("cdf.com.easypop:layout/pOp_user_a_3_tEsU"));
        assertEquals("MainActivityBinding", getDataBindingClassName(":layout/main_activity"));
        assertEquals(null, getDataBindingClassName(":layout/"));
        assertEquals(null, getDataBindingClassName(":layout"));
    }
}