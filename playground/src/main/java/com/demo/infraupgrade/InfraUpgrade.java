package com.demo.infraupgrade;

import com.demo.contract.Concept;
import com.demo.contract.Fixture;
import com.demo.mylib.*;

@Concept(description = "Represents an infrastructure upgrade operation.")
public class InfraUpgrade {
    @Fixture(description = "The identifier of the infrastructure to be upgraded.")
    public void InfraUpgradeDemo()
    {
        MyService myService = new MyService();
        String result = myService.performService();
        System.out.println(result);
    }
}
