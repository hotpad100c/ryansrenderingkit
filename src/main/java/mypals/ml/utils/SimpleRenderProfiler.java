package mypals.ml.utils;
import java.util.*;

public class SimpleRenderProfiler {

    private static class Section {
        String name;
        long totalTime;
        int count;
        long startTime;
        List<Section> children = new ArrayList<>();

        Section(String name) {
            this.name = name;
        }
    }

    private final Deque<Section> stack = new ArrayDeque<>();
    private final Section root = new Section("root");

    public SimpleRenderProfiler() {
        stack.push(root);
    }

    public void push(String name) {
        Section parent = stack.peek();
        Section section = null;

        for (Section child : parent.children) {
            if (child.name.equals(name)) {
                section = child;
                break;
            }
        }

        if (section == null) {
            section = new Section(name);
            parent.children.add(section);
        }

        stack.push(section);
        section.startTime = System.nanoTime();
        section.count++;
    }


    public void pop() {
        Section section = stack.pop();
        long duration = System.nanoTime() - section.startTime;
        section.totalTime += duration;
        //section.count++;
    }

    public void reset() {
        root.children.clear();
        stack.clear();
        stack.push(root);
    }

    public void print() {
        printSection(root, 0);
    }

    private void printSection(Section section, int indent) {
        if (!section.name.equals("root")) {
            double ms = section.totalTime / 1_000_000.0;
            String pad = "> ".repeat(indent);
            System.out.printf(pad+"\n");
            System.out.printf("%s%s: %.3f ms, count=%d, avg=%.3f ms%n",
                    pad, section.name, ms, section.count, ms / section.count);
        }
        for (Section child : section.children) {
            printSection(child, indent + 1);
        }
    }
}
