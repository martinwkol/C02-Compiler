package edu.kit.kastel.vads.compiler.backend;

import edu.kit.kastel.vads.compiler.backend.aasm.PhysicalRegister;
import edu.kit.kastel.vads.compiler.backend.aasm.VirtualRegister;
import edu.kit.kastel.vads.compiler.backend.regalloc.Register;

import java.util.*;
import java.util.stream.Collectors;

public class InterferenceGraph {
    private Set<VirtualRegister> virtualRegisters;
    private Map<Register, Set<Register>> edges;

    public void addEdge(Register register1, Register register2) {
        if (register1 instanceof PhysicalRegister && register2 instanceof PhysicalRegister) return;
        edges.computeIfAbsent(register1, _ -> new HashSet<>()).add(register2);
        edges.computeIfAbsent(register2, _ -> new HashSet<>()).add(register1);
        if (register1 instanceof VirtualRegister virtualRegister1) {
            virtualRegisters.add(virtualRegister1);
        }
        if (register1 instanceof VirtualRegister virtualRegister2) {
            virtualRegisters.add(virtualRegister2);
        }
    }

    private List<VirtualRegister> maxCardinalitySearch() {
        List<VirtualRegister> ordering = new ArrayList<>();
        Map<VirtualRegister, Integer> weight = virtualRegisters.stream()
                .collect(Collectors.toMap(r -> r, r -> 0));
        for (PhysicalRegister register : PhysicalRegister.All) {
            Set<Register> neighbourhood = edges.get(register);
            if (neighbourhood == null) continue;
            for (Register neighbour : neighbourhood) {
                if (!(neighbour instanceof VirtualRegister virtualNeighbour)) continue;
                weight.put(virtualNeighbour, weight.get(virtualNeighbour) + 1);
            }
        }

        while (!weight.isEmpty()) {
            VirtualRegister maxWeightRegister = weight.entrySet().stream()
                    .max(Comparator.comparingInt(Map.Entry::getValue))
                    .orElseThrow()
                    .getKey();
            ordering.add(maxWeightRegister);
            weight.remove(maxWeightRegister);
            for (Register neighbour : edges.get(maxWeightRegister)) {
                if (!(neighbour instanceof VirtualRegister virtualNeighbour)) continue;
                if (weight.containsKey(virtualNeighbour)) {
                    weight.put(virtualNeighbour, weight.get(virtualNeighbour) + 1);
                }
            }
        }

        return ordering;
    }
}


