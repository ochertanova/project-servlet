package com.tictactoe;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;
import java.util.List;

@WebServlet(name = "InitServlet", value = "/start")
public class InitServlet extends HttpServlet {

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {

        // Создание новой сессии
        HttpSession currentSession = req.getSession(true);

        // Создание игрового поля
        Field field = new Field();

        // Получение списка значений поля
        List<Sign> data = field.getFieldData();

        // Добавление в сессию параметров поля (нужно для хранения состояния между запросами)
        currentSession.setAttribute("field", field);
        // и значений поля, отсортированных по индексу
        currentSession.setAttribute("data", data);

        getServletContext().getRequestDispatcher("/index.jsp").forward(req, resp);

    }
}
