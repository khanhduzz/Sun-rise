package com.fjb.sunrise.controllers;

import com.example.demo.model.Category;
import com.example.demo.model.Transaction;
import com.example.demo.model.TransactionType;
import com.example.demo.service.CategoryService;
import com.example.demo.service.TransactionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

@Controller
@RequestMapping("/transactions")
public class TransactionController {
    @Autowired
    private TransactionService transactionService;
    @Autowired
    private CategoryService categoryService;

    @GetMapping
    public String listTransactions(Model model) {
        model.addAttribute("transactions", transactionService.getAllTransactions());
        return "transaction/list";
    }
    @GetMapping("/delete/{id}")
    public String deleteTransaction(@PathVariable Long id) {
        transactionService.deleteTransaction(id);
        return "redirect:/transactions";
    }
    @GetMapping("/create")
    public String showAddTransactionForm(Model model) {
        model.addAttribute("transaction", new Transaction());
        model.addAttribute("categories", categoryService.getAllCategories());
        model.addAttribute("types", TransactionType.values());
        return "transaction/create";
    }

    @PostMapping("create")
    public String addTransaction(@ModelAttribute("transaction") Transaction transaction) {
        transactionService.createTransaction(transaction);
        return "redirect:/transactions";
    }

}
