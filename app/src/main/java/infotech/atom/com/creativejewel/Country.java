package infotech.atom.com.creativejewel;

/**
 * Created by Administrator on 1/26/2016.
 */
public class Country {


    String code = null;
    String name = null;
    boolean selected = false;

    public Country(String name, boolean selected) {
        super();
        this.name = name;
        this.selected = selected;
    }


    public String getName() {
        return name;
    }
    public void setName(String name) {
        this.name = name;
    }

    public boolean isSelected() {
        return selected;
    }
    public void setSelected(boolean selected) {
        this.selected = selected;
    }

}