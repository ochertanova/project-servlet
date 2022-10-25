package com.tictactoe;

import javax.servlet.RequestDispatcher;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;
import java.util.List;

@WebServlet(name = "LogicServlet", value = "/logic")
public class LogicServlet extends HttpServlet {

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {

        // Получаем текущую сесиию
        HttpSession currentSession = req.getSession();

        // Получаем oбъект игрового поля из сесии
        Field field = extractField(currentSession);

        // Получаем индекс ячейки, по которой кликнул пользователь
        int index = getSelectIndex(req);

        // Проверяем, что ячейка, по которой был клик пустая
        // Иначе ничего не делаем и отправляем пользователя на ту же страницу
        // без изменений параметров сессии
        Sign currentSign = field.getField().get(index);

        // Проверяем, что ячейка, в которой произошел клик, пустая
        // Иначе ничего не делаем и отправляем пользователя на ту же страницу
        // без изменений параметров сессии
        if (Sign.EMPTY != currentSign) {
            RequestDispatcher requestDispatcher = getServletContext().getRequestDispatcher("/index.jsp");
            requestDispatcher.forward(req, resp);
            return;
        }

        // Ставим крестик в ячейке, по которой кликнул пользователь
        field.getField().put(index, Sign.CROSS);

        // Проверяем не победил ли нолик после последнего клика
        if (checkWin(resp, currentSession, field))
            return;

        // Получаем пустую ячейку поля
        int emptyFieldIndex = field.getEmptyFieldIndex();

        if (emptyFieldIndex >= 0) {
            field.getField().put(emptyFieldIndex, Sign.NOUGHT);
            // Проверяем не победил ли нолик после последнего клика
            if (checkWin(resp, currentSession, field))
                return;
        }
        // Если пустой ячейки нет и никто не выиграл, то значит ничья
        else {
            // Добавлям в сессию флаг, который сигнализирует, что произошла ничья
            currentSession.setAttribute("draw", true);

            // Считаем список значков
            List<Sign> data = field.getFieldData();

            // Обновляем список значков в сессии
            currentSession.setAttribute("data", data);

            resp.sendRedirect("/index.jsp");
            return;
        }

        // Считаем список значков
        List<Sign> data = field.getFieldData();

        // Обновляем объект поля и список значков в сессии
        currentSession.setAttribute("field", field);
        currentSession.setAttribute("data", data);

        resp.sendRedirect("/index.jsp");
    }

    private int getSelectIndex(HttpServletRequest req) {
        String click = req.getParameter("click");
        boolean isNumeric = click.chars().allMatch(Character::isDigit);
        return isNumeric ? Integer.parseInt(click) : 0;
    }

    private Field extractField(HttpSession currentSession) {
        Object fieldAttribute = currentSession.getAttribute("field");
        if (Field.class != fieldAttribute.getClass()) {
            currentSession.invalidate();
            throw new RuntimeException("Session is broken, ttry one more time");
        }
        return (Field) fieldAttribute;
    }

    // Метод проверяет нет ли трех крестиков-ноликов подряд
    // Возвращает true\false

    private boolean checkWin(HttpServletResponse response, HttpSession currentSession, Field field) throws IOException {
        Sign winner = field.checkWin();
        if (Sign.CROSS == winner || Sign.NOUGHT == winner) {
            // Добавляем в сессию флаг, что кто-то победил
            currentSession.setAttribute("winner", winner);

            //Считаем список значков
            List<Sign> data = field.getFieldData();

            //Обновляем список в сессии
            currentSession.setAttribute("data", data);

            // Шлем ReDirect
            response.sendRedirect("/index.jsp");
            return true;

        }
        return false;
    }


}
