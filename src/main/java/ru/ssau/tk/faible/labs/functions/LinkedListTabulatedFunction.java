package ru.ssau.tk.faible.labs.functions;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ru.ssau.tk.faible.labs.exceptions.InterpolationException;


import java.io.Serializable;
import java.util.Iterator;
import java.util.NoSuchElementException;

public class LinkedListTabulatedFunction extends AbstractTabulatedFunction implements Insertable, Removable, Serializable {
    private static final Logger log = LoggerFactory.getLogger(LinkedListTabulatedFunction.class);


    private static final long serialVersionUID = -1061918492758652804L;
    protected int count;
    private Node head;


    static class Node implements Serializable {

        private static final long serialVersionUID = -9053548425726401795L; // вложенный класс узла Node
        public Node next;
        public Node prev;
        public double x;
        public double y;

        public Node(double x, double y) {
            this.x = x;
            this.y = y;
        }
    }

    private void addNode(double x, double y) { // Добавление нового узла в конец списка
        Node newNode = new Node(x, y);
        if (head == null) {
            newNode.next = newNode;
            newNode.prev = newNode;
            head = newNode;
        } else {
            Node last = head.prev;
            last.next = newNode;
            head.prev = newNode;
            newNode.next = head;
            newNode.prev = last;
        }
        count++;
    }

    private static boolean isEqual(double first, double second) { // метод для проверки на равенство двух double чисел
        return Math.abs(first - second) <= 1e-6;
    }

    public LinkedListTabulatedFunction(double[] xValues, double[] yValues) { // первый конструктор для двух массивов
        AbstractTabulatedFunction.checkLengthIsTheSame(xValues, yValues);
        if (xValues.length < 2) {
            log.error("В конструктор LinkedListTabulatedFunction передан массив xValues длины <2");
            throw new IllegalArgumentException("Длина таблицы не может быть меньше 2");
        }
        AbstractTabulatedFunction.checkSorted(xValues);
        for (int i = 0; i < xValues.length; i++) {
            addNode(xValues[i], yValues[i]);
        }
    }

    // второй конструктор для математической функции
    public LinkedListTabulatedFunction(MathFunction source, double xFrom, double xTo, int count) {
        if (count < 2) {
            log.error("В конструктор LinkedListTabulatedFunction передано число точек <2");
            throw new IllegalArgumentException("Длина таблицы не может быть меньше 2");
        }
        if (xFrom > xTo) { // если xFrom > xTo, меняем местами
            double temp = xFrom;
            xFrom = xTo;
            xTo = temp;
        }
        if (isEqual(xFrom, xTo)) { // если xFrom = xTo
            double yValue = source.apply(xFrom);
            for (int i = 0; i < count; i++) {
                addNode(xFrom, yValue);
            }
        } else { // равномерная дискретизация
            double step = (xTo - xFrom) / (count - 1); // шаг дискретизации
            for (int i = 0; i < count; i++) {
                double x = xFrom + i * step;
                double y = source.apply(x);
                addNode(x, y);
            }
        }
    }

    @Override
    public void remove(int index) {
        if (index < 0 || index >= count) {
            log.error("Вызван метод remove для некорректного индекса");
            throw new IllegalArgumentException("Индекс выходит за границы длины таблицы");
        }

        Node removable = getNode(index);

        if (count == 1) { // если элемент единственный, очищаем голову
            head = null;
        } else {
            removable.prev.next = removable.next; // связываем следующий и предыдущие узлы
            removable.next.prev = removable.prev;

            if (removable == head) {
                head = head.next;
            }
        }

        removable.next = null;
        removable.prev = null;
        count--;
    }

    @Override
    public void insert(double x, double y) {
        // если список пустой, просто добавляем узел
        if (head == null) {
            addNode(x, y);
            return;
        }

        // проверяем, существует ли уже узел с таким x
        for (int i = 0; i < count; i++) {
            Node currentNode = getNode(i);
            if (isEqual(currentNode.x, x)) {
                // если нашли узел с таким x, обновляем y и выходим
                currentNode.y = y;
                log.debug("метод insert изменил уже существующее значение x");
                return;
            }
        }

        // создаем новый узел
        Node newNode = new Node(x, y);

        // если новый узел должен быть в начале списка
        if (x < head.x) {
            Node last = head.prev;

            // устанавливаем связи для нового узла
            newNode.next = head;
            newNode.prev = last;

            // обновляем связи соседних узлов
            head.prev = newNode;
            last.next = newNode;

            // обновляем голову списка
            head = newNode;
            count++;
            return;
        }

        // если новый узел должен быть в конце списка
        if (x > head.prev.x) {
            Node last = head.prev;

            // устанавливаем связи для нового узла
            newNode.next = head;
            newNode.prev = last;

            // обновляем связи соседних узлов
            last.next = newNode;
            head.prev = newNode;

            count++;
            return;
        }

        // поиск места для вставки в середину списка
        Node current = head;
        for (int i = 0; i < count; i++) {
            if (current.x < x && x < current.next.x) {
                // нашли место для вставки между current и current.next
                newNode.next = current.next;
                newNode.prev = current;

                // обновляем связи соседних узлов
                current.next.prev = newNode;
                current.next = newNode;

                count++;
                return;
            }
            current = current.next;
        }
    }

    private Node getNode(int index) { // метод для получения узла по индексу
        if (index < 0 || index >= count) {
            log.error("Вызван метод getNode для некорректного индекса");
            throw new IllegalArgumentException("Индекс выходит за границы длины таблицы");
        }
        Node currentNode = head;
        if (index <= count / 2) {
            for (int i = 0; i < index; i++) {
                currentNode = currentNode.next;
            }
        } else {
            for (int i = 0; i < (count - index); i++) {
                currentNode = currentNode.prev;
            }
        }
        return currentNode;
    }

    @Override
    public Iterator<Point> iterator() {
        return new Iterator<Point>() {
            private Node node = head;

            @Override
            public boolean hasNext() {
                return node != null;
            }

            @Override
            public Point next() {
                if (!hasNext()) {
                    log.error("Вызван метод next когда элементов не осталось");
                    throw new NoSuchElementException("В таблице не осталось элементов");
                }
                Point point = new Point(node.x, node.y); // создаем объект Point

                if (node.next == head) { // если текущий элемент последний
                    node = null;
                } else {
                    node = node.next;
                }
                return point;
            }
        };
    }

    @Override
    public int getCount() { // геттер для count
        return count;
    }

    @Override
    public double leftBound() { // самый левый x
        return head.x;
    }

    @Override
    public double rightBound() { // самый правый x
        return head.prev.x;
    }

    @Override
    public double getX(int index) { // нахождение x по индексу
        if (index < 0 || index >= count) {
            log.error("Вызван метод getX для некорректного индекса");
            throw new IllegalArgumentException("Индекс выходит за границы длины таблицы");
        }
        return getNode(index).x;
    }

    @Override
    public double getY(int index) { // нахождение y по индексу
        if (index < 0 || index >= count) {
            log.error("Вызван метод getY для некорректного индекса");
            throw new IllegalArgumentException("Индекс выходит за границы длины таблицы");
        }
        return getNode(index).y;
    }

    @Override
    public void setY(int index, double value) { // сеттер для y по индексу
        if (index < 0 || index >= count) {
            log.error("Вызван метод setY для некорректного индекса");
            throw new IllegalArgumentException("Индекс выходит за границы длины таблицы");
        }
        getNode(index).y = value;
    }

    @Override
    public int indexOfX(double x) { // нахождение индекса х по значению
        for (int i = 0; i < count; i++) {
            if (isEqual(getX(i), x)) {
                return i;
            }
        }
        return -1;
    }

    @Override
    public int indexOfY(double y) { // нахождение индекса y по значению
        for (int i = 0; i < count; i++) {
            if (isEqual(getY(i), y)) {
                return i;
            }
        }
        return -1;
    }

    @Override
    protected int floorIndexOfX(double x) { // нахождение промежутка для x по значению
        if (x < getX(0)) { // если x меньше левой границы
            log.error("Вызван метод floorIndexOfX для некорректного x");
            throw new IllegalArgumentException("X меньше левой границы");
        }
        if (x > getX(count - 1)) { // если x больше всех элементов
            return count;
        }
        for (int i = 0; i < count - 1; i++) {
            double currentX = getX(i); // текущий х
            double nextX = getX(i + 1); // следующий х

            if (isEqual(currentX, x)) { // если нашли точное совпадение
                return i; // возвращаем его индекс
            }

            if (currentX < x && x < nextX) { // если x находится в интервале (currentX, nextX)
                return i; // возвращаем currentX
            }
        }
        return count - 1;
    }

    @Override
    protected double interpolate(double x, int floorIndex) { // интерполяция для промежутка
        // границы интервала
        double leftX = getX(floorIndex);
        double rightX = getX(floorIndex + 1);

        if (x < leftX || x > rightX) {
            log.error("Вызван метод interpolate для некорректного x (вне зоны интерполяции)");
            throw new InterpolationException("x вне зоны интерполяции"); // бросаем исключение
        }
        return interpolate(x, getX(floorIndex), getX(floorIndex + 1), getY(floorIndex), getY(floorIndex + 1));
    }

    @Override
    protected double extrapolateLeft(double x) { // экстраполяция слева <=> интерполяция от самого левого промежутка
        return interpolate(x, getX(0), getX(1), getY(0), getY(1));
    }

    @Override
    protected double extrapolateRight(double x) { // экстраполяция справа <=> интерполяция от самого правого промежутка
        return interpolate(x, getX(count - 2), getX(count - 1), getY(count - 2), getY(count - 1));
    }
}
