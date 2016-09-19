package infotech.atom.com.creativejewel;

/**
 * Created by Administrator on 2/14/2016.
 */
public class Orders_model {

    String order_no,volume1,volume2,volume3,new_arrivals,created_date;

    public String getOrder_no() {
        return order_no;
    }

    public Orders_model()
    {}

    public Orders_model(String order_no, String volume1, String volume2, String volume3, String new_arrivals, String created_date) {
        this.order_no = order_no;
        this.volume1 = volume1;
        this.volume2 = volume2;
        this.volume3 = volume3;
        this.new_arrivals = new_arrivals;
        this.created_date = created_date;
    }

    public void setOrder_no(String order_no) {
        this.order_no = order_no;
    }

    public String getVolume1() {
        return volume1;
    }

    public void setVolume1(String volume1) {
        this.volume1 = volume1;
    }

    public String getVolume2() {
        return volume2;
    }

    public void setVolume2(String volume2) {
        this.volume2 = volume2;
    }

    public String getVolume3() {
        return volume3;
    }

    public void setVolume3(String volume3) {
        this.volume3 = volume3;
    }

    public String getNew_arrivals() {
        return new_arrivals;
    }

    public void setNew_arrivals(String new_arrivals) {
        this.new_arrivals = new_arrivals;
    }

    public String getCreated_date() {
        return created_date;
    }

    public void setCreated_date(String created_date) {
        this.created_date = created_date;
    }
}
