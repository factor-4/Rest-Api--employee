package com.example.demo.rest;

import com.example.demo.dao.EmployeeDAO;
import com.example.demo.entity.Employee;
import com.example.demo.service.EmployeeService;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api")
public class EmployeeRestController {


    private EmployeeService employeeService;

    private ObjectMapper objectMapper;
    @Autowired
    public EmployeeRestController(EmployeeService theEmployeeService, ObjectMapper theObjectMapper) {
        employeeService = theEmployeeService;
        objectMapper = theObjectMapper;
    }

    // expose "/employees" and return a list of employees
    @GetMapping("/employees")
    public List<Employee> findAll() {
        return employeeService.findAll();
    }

    // add mapping for get a employee
    @GetMapping("/employees/{employeeId}")
    public Employee getEmployee(@PathVariable int employeeId) {
        Employee theEmployee = employeeService.findById(employeeId);

        if(theEmployee == null){
            throw new RuntimeException(("Employee id not found - "+ employeeId));

        }

        return  theEmployee;
    }


    // add mapping for post / employee - add new employee

    @PostMapping("/employees")
    public Employee addEmployee(@RequestBody Employee theEmployee)  {
        // also just in case they pass an id in json-- set id to 0

        // this is to force save of new item ... instead of update

        theEmployee.setId(0);
        Employee dbEmployee = employeeService.save(theEmployee);

        return dbEmployee;

    }

    // add mapping for updated

    @PutMapping("/employees")
    public Employee updateEmployee(@RequestBody Employee theEmployee) {
        Employee dbEmployee = employeeService.save(theEmployee);

        return dbEmployee;

    }

    // add mapping for patch
    @PatchMapping("/employees/{employeeId}")
    public Employee patchEmployee(@PathVariable int employeeId,
                                  @RequestBody Map<String, Object> patchPayload){

        Employee tempEmployee = employeeService.findById(employeeId);

        // throw exception if no employee

        if(tempEmployee == null) {

            throw new RuntimeException(("Employee id not found - " + employeeId));

        }
        // throw exception if request body contains "id"
        
        if(patchPayload.containsKey("id")){
            throw new RuntimeException(("Employee id not not allowed in request body  " + employeeId));

        }
        
        Employee patchedEmployee = apply(patchPayload, tempEmployee);

        Employee dbEmployee = employeeService.save(patchedEmployee);

        return dbEmployee;
        
        

    }

    private Employee apply(Map<String, Object> patchPayload, Employee tempEmployee) {
        // convert employee object to a json object node
        ObjectNode employeeNode = objectMapper.convertValue(tempEmployee, ObjectNode.class);

        ObjectNode patchNode = objectMapper.convertValue(patchPayload, ObjectNode.class);

        // Merge the patch updated into the employee node

        employeeNode.setAll(patchNode);

        return objectMapper.convertValue(employeeNode, Employee.class);
    }



    @DeleteMapping("/employees/{employeeId}")
    public String deleteEmployee(@PathVariable int employeeId){
        Employee thempEmployee = employeeService.findById((employeeId));

        if(thempEmployee == null){
            throw new RuntimeException(("Employee id not found "+ employeeId));

        }

        employeeService.deleteById((employeeId));

        return "Deleted emoployee id -"+employeeId;
    }

}
 