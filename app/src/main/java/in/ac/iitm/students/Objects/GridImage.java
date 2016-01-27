package in.ac.iitm.students.Objects;

/**
 * Created by arunp on 06-Nov-15.
 */
public class GridImage {
    public String img,description;

    public GridImage(String img, String description) {
        this.img = img;
        this.description = description;
    }

    public String getImg() {
        return img;
    }

    public void setImg(String img) {
        this.img = img;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }
}
