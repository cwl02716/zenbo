package com.example.bluesnow_zenbo2;

import java.util.HashMap;
import java.util.Map;

public class Action
{
    private String _act;

    public Action(String act)
    {
        _act=act;
    }
    public void setact(String act)
    {
        _act=act;
    }
    public Map Mapping(){
        Map<String,String> map = new HashMap<String,String>();
        map.put("act",_act);
        return map;
    }

}
