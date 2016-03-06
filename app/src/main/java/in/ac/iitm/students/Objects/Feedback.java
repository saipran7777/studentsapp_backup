package in.ac.iitm.students.Objects;

import java.util.ArrayList;

/**
 * Created by arunp on 02-Mar-16.
 */
public class Feedback {
    int id;
    int anonymous;
    int solved;
    float avg_anger;
    String title, content, created_at,user_name;
    ArrayList<Tag> tags;
    ArrayList<FeedbackComment> comments;

    public String getUser_name() {
        if (user_name==null){
            return  "";
        }else {
            return  UppercaseFirstLetters(user_name.toLowerCase());

        }

    }

    public void setUser_name(String user_name) {
        this.user_name = user_name;
    }

    public Feedback(int id, int anonymous, int solved, float avg_anger, String title, String content,
                    String created_at, String user_name, ArrayList<Tag> tags, ArrayList<FeedbackComment> comments) {
        this.id = id;
        this.anonymous = anonymous;
        this.solved = solved;
        this.avg_anger = avg_anger;
        this.title = title;
        this.content = content;
        this.created_at = created_at;
        this.user_name = UppercaseFirstLetters(user_name.toLowerCase());
        this.tags = tags;
        this.comments = comments;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getAnonymous() {
        return anonymous;
    }

    public void setAnonymous(int anonymous) {
        this.anonymous = anonymous;
    }

    public int getSolved() {
        return solved;
    }

    public void setSolved(int solved) {
        this.solved = solved;
    }

    public float getAvg_anger() {
        return avg_anger;
    }

    public void setAvg_anger(float avg_anger) {
        this.avg_anger = avg_anger;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public String getCreated_at() {
        return created_at;
    }

    public void setCreated_at(String created_at) {
        this.created_at = created_at;
    }

    public ArrayList<Tag> getTags() {
        return tags;
    }

    public void setTags(ArrayList<Tag> tags) {
        this.tags = tags;
    }

    public ArrayList<FeedbackComment> getComments() {
        return comments;
    }

    public void setComments(ArrayList<FeedbackComment> comments) {
        this.comments = comments;
    }

    public class Tag{
        String tagname_name;
        int tag_id;

        public Tag(String tagname_name, int tag_id) {
            this.tagname_name = tagname_name;
            this.tag_id = tag_id;
        }

        public String getTagname_name() {
            return tagname_name;
        }

        public void setTagname_name(String tagname_name) {
            this.tagname_name = tagname_name;
        }

        public int getTag_id() {
            return tag_id;
        }

        public void setTag_id(int tag_id) {
            this.tag_id = tag_id;
        }
    }
    public class FeedbackComment {
        String user_name;
        String created_at;
        String content;

        public FeedbackComment(String user_name, String created_at, String content) {
            this.user_name = UppercaseFirstLetters(user_name.toLowerCase());
            this.created_at = created_at;
            this.content = content;
        }

        public String getUser_name() {
            if (user_name==null){
                return  "";
            }else {
                return  UppercaseFirstLetters(user_name.toLowerCase());

            }

        }

        public void setUser_name(String user_name) {
            this.user_name = user_name;
        }

        public String getCreated_at() {
            return created_at;
        }

        public void setCreated_at(String created_at) {
            this.created_at = created_at;
        }

        public String getContent() {
            return content;
        }

        public void setContent(String content) {
            this.content = content;
        }
    }
    public static String UppercaseFirstLetters(String str)
    {
        boolean prevWasWhiteSp = true;
        char[] chars = str.toCharArray();
        for (int i = 0; i < chars.length; i++) {
            if (Character.isLetter(chars[i])) {
                if (prevWasWhiteSp) {
                    chars[i] = Character.toUpperCase(chars[i]);
                }
                prevWasWhiteSp = false;
            } else {
                prevWasWhiteSp = Character.isWhitespace(chars[i]);
            }
        }
        return new String(chars);
    }
}
