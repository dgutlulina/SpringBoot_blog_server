package lln.spring.tools;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.HashMap;
import java.util.Map;

@Data
@AllArgsConstructor
public class Result {
    private boolean success = true;
    private String msg = "";
    private Map<String, Object> map = new HashMap<>();
    public Result(boolean success, String msg) {
        this.success = success;
        this.msg = msg;
    }

    public Result() {

    }


    public void  setError(String msg) {
        this.success = false;
        this.msg = msg;
    }


    public void setErrorMessage(String s) {
        this.success = false;
        this.msg = s;
    }
}