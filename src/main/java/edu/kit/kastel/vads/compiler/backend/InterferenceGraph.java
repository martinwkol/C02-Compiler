package edu.kit.kastel.vads.compiler.backend;

import edu.kit.kastel.vads.compiler.backend.aasm.PhysicalRegister;
import edu.kit.kastel.vads.compiler.backend.aasm.VirtualRegister;
import edu.kit.kastel.vads.compiler.backend.regalloc.Register;

import java.util.HashSet;
import java.util.Map;
import java.util.Set;

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

    
}


