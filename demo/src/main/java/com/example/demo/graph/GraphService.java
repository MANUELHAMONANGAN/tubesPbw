package com.example.demo.graph;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class GraphService {
    @Autowired
    private TransaksiGraphRepository transaksiGraphRepository;

    public List<TransaksiGraph> getAllGraphData(){
        return this.transaksiGraphRepository.getAllGraphData();
    }
}
