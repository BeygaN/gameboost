package com.example.gamepulse;

import android.app.Activity;
import android.graphics.Color;
import android.graphics.Typeface;
import android.graphics.drawable.GradientDrawable;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.text.InputType;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.HorizontalScrollView;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Locale;

public class MainActivity extends Activity {

    private static final int BG = Color.rgb(10, 16, 32);
    private static final int SURFACE = Color.rgb(18, 29, 50);
    private static final int SURFACE_LIGHT = Color.rgb(27, 41, 67);
    private static final int ACCENT = Color.rgb(124, 255, 178);
    private static final int WHITE = Color.rgb(243, 247, 255);
    private static final int MUTED = Color.rgb(158, 171, 194);
    private static final int STROKE = Color.rgb(41, 57, 84);

    private FrameLayout contentFrame;
    private TextView navExplore;
    private TextView navPerformance;
    private LinearLayout gameList;
    private String selectedCategory = "Todos";
    private String searchText = "";
    private String prefilledGameName = "";
    private final ArrayList<Game> games = new ArrayList<>();
    private final ArrayList<SessionRecord> sessions = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        getWindow().setStatusBarColor(BG);
        getWindow().setNavigationBarColor(BG);

        contentFrame = findViewById(R.id.contentFrame);
        navExplore = findViewById(R.id.navExplore);
        navPerformance = findViewById(R.id.navPerformance);

        loadGames();
        loadSessions();

        navExplore.setOnClickListener(v -> showExplore());
        navPerformance.setOnClickListener(v -> { prefilledGameName = ""; showPerformance(); });
        showExplore();
    }

    private void loadGames() {
        games.add(new Game(
                "Genshin Impact", "RPG · Aventura", "GI", "ALTA",
                "Explora un mundo abierto, completa misiones y combate usando habilidades elementales.",
                "Usa gráficos medios, limita los FPS a un valor estable y vigila la temperatura durante sesiones largas.",
                "Gama media-alta", "30–60 FPS orientativos"));
        games.add(new Game(
                "Call of Duty: Mobile", "Acción · Shooter", "COD", "ALTA",
                "Shooter de acción con partidas multijugador y un modo Battle Royale.",
                "Prioriza la tasa de fotogramas sobre los efectos gráficos. Prueba calidad media y aumenta los FPS si el equipo lo permite.",
                "Gama media o superior", "60 FPS o más, según el equipo"));
        games.add(new Game(
                "Minecraft", "Creatividad · Sandbox", "MC", "MEDIA",
                "Construye, explora y sobrevive en mundos generados por bloques.",
                "Ajusta la distancia de renderizado y reduce efectos visuales si aparecen caídas de rendimiento.",
                "Gama de entrada o superior", "30–60 FPS orientativos"));
        games.add(new Game(
                "Asphalt 9: Legends", "Carreras · Arcade", "A9", "ALTA",
                "Juego de carreras arcade con vehículos, circuitos y eventos competitivos.",
                "Comienza con gráficos equilibrados. Una temperatura controlada ayuda a mantener la fluidez.",
                "Gama media o superior", "30–60 FPS orientativos"));
    }

    private void showExplore() {
        updateNavigation(true);
        contentFrame.removeAllViews();

        LinearLayout page = pageContainer();
        addText(page, "CENTRO DE JUEGOS", 10, ACCENT, true, 0, 0, 0, 8);
        addText(page, "Todo sobre tus juegos.", 27, WHITE, true, 0, 0, 0, 4);
        addText(page, "Consulta sus características y descubre cómo ajustar el rendimiento en Android.",
                14, MUTED, false, 0, 0, 0, 18);

        LinearLayout metrics = new LinearLayout(this);
        metrics.setOrientation(LinearLayout.HORIZONTAL);
        metrics.addView(metricCard("04", "JUEGOS"), weightedParams(1, 0, 6));
        metrics.addView(metricCard("FPS", "REGISTRO"), weightedParams(1, 0, 6));
        metrics.addView(metricCard("LOCAL", "TUS DATOS"), weightedParams(1, 0, 0));
        page.addView(metrics, matchWrap());

        addText(page, "Explorar biblioteca", 18, WHITE, true, 0, 22, 0, 10);
        EditText search = new EditText(this);
        search.setSingleLine(true);
        search.setTextSize(14);
        search.setTextColor(WHITE);
        search.setHintTextColor(MUTED);
        search.setHint("Buscar un juego...");
        search.setPadding(dp(15), dp(11), dp(15), dp(11));
        search.setBackground(roundBackground(SURFACE, STROKE, 14));
        search.setCompoundDrawablePadding(dp(8));
        search.setText(searchText);
        page.addView(search, new LinearLayout.LayoutParams(-1, dp(48)));

        LinearLayout chips = new LinearLayout(this);
        chips.setOrientation(LinearLayout.HORIZONTAL);
        chips.setPadding(0, dp(2), 0, dp(2));
        String[] categories = {"Todos", "RPG", "Acción", "Carreras", "Creatividad"};
        for (String category : categories) {
            TextView chip = new TextView(this);
            chip.setText(category);
            chip.setTextSize(12);
            chip.setTypeface(null, Typeface.BOLD);
            chip.setGravity(Gravity.CENTER);
            chip.setPadding(dp(14), dp(9), dp(14), dp(9));
            boolean selected = selectedCategory.equals(category);
            chip.setTextColor(selected ? BG : MUTED);
            chip.setBackground(roundBackground(selected ? ACCENT : SURFACE, selected ? ACCENT : STROKE, 22));
            LinearLayout.LayoutParams chipParams = new LinearLayout.LayoutParams(-2, -2);
            chipParams.setMargins(0, 0, dp(8), 0);
            chips.addView(chip, chipParams);
            chip.setOnClickListener(v -> {
                selectedCategory = category;
                showExplore();
            });
        }
        HorizontalScrollView chipScroll = new HorizontalScrollView(this);
        chipScroll.setFillViewport(false);
        chipScroll.setHorizontalScrollBarEnabled(false);
        chipScroll.addView(chips);
        page.addView(chipScroll, new LinearLayout.LayoutParams(-1, dp(48)));

        gameList = new LinearLayout(this);
        gameList.setOrientation(LinearLayout.VERTICAL);
        page.addView(gameList, matchWrap());
        refreshGameCards();

        addText(page, "Los perfiles son orientativos. El resultado real depende del modelo del celular, la versión del juego y la configuración.",
                11, MUTED, false, 2, 18, 2, 8);

        addScrollPage(page);

        search.addTextChangedListener(new TextWatcher() {
            @Override public void beforeTextChanged(CharSequence s, int start, int count, int after) { }
            @Override public void onTextChanged(CharSequence s, int start, int before, int count) {
                searchText = s.toString();
                refreshGameCards();
            }
            @Override public void afterTextChanged(Editable s) { }
        });
    }

    private void refreshGameCards() {
        if (gameList == null) return;
        gameList.removeAllViews();
        int found = 0;
        for (Game game : games) {
            String search = searchText.toLowerCase(Locale.ROOT).trim();
            boolean matchesSearch = game.name.toLowerCase(Locale.ROOT).contains(search)
                    || game.genre.toLowerCase(Locale.ROOT).contains(search);
            boolean matchesCategory = selectedCategory.equals("Todos")
                    || game.genre.toLowerCase(Locale.ROOT).contains(selectedCategory.toLowerCase(Locale.ROOT));
            if (matchesSearch && matchesCategory) {
                gameList.addView(gameCard(game), matchWrap());
                found++;
            }
        }
        if (found == 0) {
            TextView empty = text("No se encontraron juegos con esa búsqueda.", 13, MUTED, false);
            empty.setPadding(dp(5), dp(18), dp(5), dp(18));
            gameList.addView(empty);
        }
    }

    private View gameCard(Game game) {
        LinearLayout card = new LinearLayout(this);
        card.setOrientation(LinearLayout.HORIZONTAL);
        card.setGravity(Gravity.CENTER_VERTICAL);
        card.setPadding(dp(14), dp(14), dp(14), dp(14));
        card.setBackground(roundBackground(SURFACE, STROKE, 18));

        TextView icon = text(game.shortName, 16, ACCENT, true);
        icon.setGravity(Gravity.CENTER);
        icon.setBackground(roundBackground(SURFACE_LIGHT, STROKE, 14));
        card.addView(icon, new LinearLayout.LayoutParams(dp(54), dp(54)));

        LinearLayout info = new LinearLayout(this);
        info.setOrientation(LinearLayout.VERTICAL);
        info.setPadding(dp(12), 0, dp(5), 0);
        TextView name = text(game.name, 15, WHITE, true);
        info.addView(name);
        TextView genre = text(game.genre, 11, MUTED, false);
        LinearLayout.LayoutParams genreParams = matchWrap();
        genreParams.topMargin = dp(4);
        info.addView(genre, genreParams);
        TextView load = text("EXIGENCIA " + game.load, 9, ACCENT, true);
        LinearLayout.LayoutParams loadParams = matchWrap();
        loadParams.topMargin = dp(7);
        info.addView(load, loadParams);
        card.addView(info, new LinearLayout.LayoutParams(0, -2, 1));

        TextView arrow = text("›", 28, MUTED, false);
        card.addView(arrow, new LinearLayout.LayoutParams(-2, -2));
        card.setOnClickListener(v -> showGameDetail(game));

        LinearLayout.LayoutParams params = matchWrap();
        params.bottomMargin = dp(10);
        card.setLayoutParams(params);
        return card;
    }

    private void showGameDetail(Game game) {
        updateNavigation(true);
        contentFrame.removeAllViews();
        LinearLayout page = pageContainer();
        TextView back = text("‹  Volver a juegos", 13, ACCENT, true);
        back.setPadding(0, 0, 0, dp(18));
        back.setOnClickListener(v -> showExplore());
        page.addView(back);

        TextView logo = text(game.shortName, 22, ACCENT, true);
        logo.setGravity(Gravity.CENTER);
        logo.setBackground(roundBackground(SURFACE_LIGHT, STROKE, 20));
        page.addView(logo, new LinearLayout.LayoutParams(dp(74), dp(74)));
        addText(page, game.name, 26, WHITE, true, 0, 16, 0, 4);
        addText(page, game.genre + "  ·  Exigencia " + game.load.toLowerCase(Locale.ROOT), 12, MUTED, false, 0, 0, 0, 18);

        LinearLayout summary = cardContainer();
        addText(summary, "SOBRE EL JUEGO", 10, ACCENT, true, 0, 0, 0, 8);
        addText(summary, game.description, 14, WHITE, false, 0, 0, 0, 0);
        page.addView(summary, matchWrap());

        LinearLayout performance = cardContainer();
        addText(performance, "PERFIL DE RENDIMIENTO", 10, ACCENT, true, 0, 0, 0, 10);
        addText(performance, "Carga estimada: " + game.load, 15, WHITE, true, 0, 0, 0, 8);
        addText(performance, "Referencia de FPS: " + game.fps, 13, WHITE, false, 0, 0, 0, 8);
        addText(performance, "Equipo sugerido: " + game.device, 13, MUTED, false, 0, 0, 0, 10);
        addText(performance, game.tips, 13, MUTED, false, 0, 0, 0, 0);
        LinearLayout.LayoutParams perfParams = matchWrap();
        perfParams.topMargin = dp(12);
        page.addView(performance, perfParams);

        TextView register = actionButton("Registrar una prueba de este juego");
        LinearLayout.LayoutParams buttonParams = matchWrap();
        buttonParams.topMargin = dp(16);
        page.addView(register, buttonParams);
        register.setOnClickListener(v -> {
            prefilledGameName = game.name;
            showPerformance();
        });
        addText(page, "Nota: FPS y exigencia son referencias educativas, no mediciones en vivo ni requisitos oficiales del desarrollador.",
                11, MUTED, false, 2, 14, 2, 6);
        addScrollPage(page);
    }

    private void showPerformance() {
        updateNavigation(false);
        contentFrame.removeAllViews();
        LinearLayout page = pageContainer();
        addText(page, "ANÁLISIS PERSONAL", 10, ACCENT, true, 0, 0, 0, 8);
        addText(page, "Mide tu experiencia.", 26, WHITE, true, 0, 0, 0, 5);
        addText(page, "Registra los datos que observes mientras juegas y consulta tu puntuación.",
                14, MUTED, false, 0, 0, 0, 18);

        LinearLayout form = cardContainer();
        addText(form, "NUEVA PRUEBA", 10, ACCENT, true, 0, 0, 0, 13);
        addText(form, "Nombre del juego", 12, MUTED, true, 0, 0, 0, 6);
        EditText gameInput = input("Ej. Genshin Impact", InputType.TYPE_CLASS_TEXT);
        if (!prefilledGameName.isEmpty()) {
            gameInput.setText(prefilledGameName);
            prefilledGameName = "";
        }
        form.addView(gameInput, new LinearLayout.LayoutParams(-1, dp(48)));

        LinearLayout values = new LinearLayout(this);
        values.setOrientation(LinearLayout.HORIZONTAL);
        LinearLayout fpsColumn = new LinearLayout(this);
        fpsColumn.setOrientation(LinearLayout.VERTICAL);
        addText(fpsColumn, "FPS promedio", 12, MUTED, true, 0, 14, 0, 6);
        EditText fpsInput = input("Ej. 60", InputType.TYPE_CLASS_NUMBER | InputType.TYPE_NUMBER_FLAG_DECIMAL);
        fpsColumn.addView(fpsInput, new LinearLayout.LayoutParams(-1, dp(48)));
        LinearLayout tempColumn = new LinearLayout(this);
        tempColumn.setOrientation(LinearLayout.VERTICAL);
        addText(tempColumn, "Temperatura °C", 12, MUTED, true, 0, 14, 0, 6);
        EditText tempInput = input("Ej. 38", InputType.TYPE_CLASS_NUMBER | InputType.TYPE_NUMBER_FLAG_DECIMAL);
        tempColumn.addView(tempInput, new LinearLayout.LayoutParams(-1, dp(48)));
        values.addView(fpsColumn, new LinearLayout.LayoutParams(0, -2, 1));
        LinearLayout.LayoutParams tempParams = new LinearLayout.LayoutParams(0, -2, 1);
        tempParams.leftMargin = dp(10);
        values.addView(tempColumn, tempParams);
        form.addView(values, matchWrap());

        TextView save = actionButton("Guardar y analizar");
        LinearLayout.LayoutParams saveParams = matchWrap();
        saveParams.topMargin = dp(18);
        form.addView(save, saveParams);
        page.addView(form, matchWrap());

        LinearLayout resultArea = new LinearLayout(this);
        resultArea.setOrientation(LinearLayout.VERTICAL);
        LinearLayout.LayoutParams resultParams = matchWrap();
        resultParams.topMargin = dp(14);
        page.addView(resultArea, resultParams);

        save.setOnClickListener(v -> {
            String gameName = gameInput.getText().toString().trim();
            String fpsText = fpsInput.getText().toString().trim().replace(',', '.');
            String tempText = tempInput.getText().toString().trim().replace(',', '.');
            if (gameName.isEmpty() || fpsText.isEmpty() || tempText.isEmpty()) {
                Toast.makeText(this, "Completa el juego, los FPS y la temperatura.", Toast.LENGTH_SHORT).show();
                return;
            }
            try {
                double fps = Double.parseDouble(fpsText);
                double temperature = Double.parseDouble(tempText);
                if (fps <= 0 || fps > 240) {
                    Toast.makeText(this, "Ingresa FPS entre 1 y 240.", Toast.LENGTH_SHORT).show();
                    return;
                }
                if (temperature < 10 || temperature > 100) {
                    Toast.makeText(this, "Ingresa una temperatura entre 10 y 100 °C.", Toast.LENGTH_SHORT).show();
                    return;
                }
                SessionRecord record = new SessionRecord(gameName, fps, temperature, System.currentTimeMillis());
                sessions.add(0, record);
                while (sessions.size() > 10) sessions.remove(sessions.size() - 1);
                saveSessions();
                resultArea.removeAllViews();
                resultArea.addView(resultCard(record), matchWrap());
                renderHistory(resultArea);
                Toast.makeText(this, "Prueba guardada correctamente.", Toast.LENGTH_SHORT).show();
            } catch (NumberFormatException ex) {
                Toast.makeText(this, "Revisa los valores numéricos.", Toast.LENGTH_SHORT).show();
            }
        });

        if (!sessions.isEmpty()) {
            resultArea.addView(resultCard(sessions.get(0)), matchWrap());
            renderHistory(resultArea);
        } else {
            LinearLayout empty = cardContainer();
            addText(empty, "Aún no tienes pruebas guardadas.", 13, MUTED, false, 0, 0, 0, 0);
            resultArea.addView(empty, matchWrap());
        }

        addText(page, "Importante: esta app no lee los FPS ni la temperatura de otros juegos automáticamente. Registra aquí los valores que observes con las herramientas de tu dispositivo.",
                11, MUTED, false, 2, 16, 2, 8);
        addScrollPage(page);
    }

    private View resultCard(SessionRecord record) {
        int score = calculateScore(record.fps, record.temperature);
        String rating = score >= 90 ? "Excelente" : score >= 75 ? "Bueno" : score >= 55 ? "Regular" : "Bajo";
        LinearLayout card = cardContainer();
        addText(card, "ÚLTIMO ANÁLISIS", 10, ACCENT, true, 0, 0, 0, 8);
        addText(card, record.game, 17, WHITE, true, 0, 0, 0, 10);
        LinearLayout stats = new LinearLayout(this);
        stats.setOrientation(LinearLayout.HORIZONTAL);
        stats.addView(metricCard(String.format(Locale.getDefault(), "%.0f", record.fps), "FPS"), weightedParams(1, 0, 6));
        stats.addView(metricCard(String.format(Locale.getDefault(), "%.1f°", record.temperature), "TEMP."), weightedParams(1, 0, 6));
        stats.addView(metricCard(String.valueOf(score) + "/100", "PUNTUACIÓN"), weightedParams(1, 0, 0));
        card.addView(stats, matchWrap());
        addText(card, "Resultado: " + rating, 14, ACCENT, true, 0, 12, 0, 4);
        addText(card, recommendation(record.fps, record.temperature), 12, MUTED, false, 0, 0, 0, 0);
        return card;
    }

    private void renderHistory(LinearLayout parent) {
        addText(parent, "Historial reciente", 17, WHITE, true, 0, 16, 0, 10);
        if (sessions.size() <= 1) {
            addText(parent, "Tus próximas pruebas aparecerán aquí.", 12, MUTED, false, 0, 0, 0, 0);
            return;
        }
        for (int i = 1; i < sessions.size(); i++) {
            SessionRecord record = sessions.get(i);
            LinearLayout row = new LinearLayout(this);
            row.setOrientation(LinearLayout.HORIZONTAL);
            row.setGravity(Gravity.CENTER_VERTICAL);
            row.setPadding(dp(13), dp(11), dp(13), dp(11));
            row.setBackground(roundBackground(SURFACE, STROKE, 14));
            LinearLayout details = new LinearLayout(this);
            details.setOrientation(LinearLayout.VERTICAL);
            details.addView(text(record.game, 13, WHITE, true));
            String date = new SimpleDateFormat("dd/MM  HH:mm", Locale.getDefault()).format(new Date(record.timestamp));
            details.addView(text(date + "  ·  " + String.format(Locale.getDefault(), "%.0f FPS", record.fps), 10, MUTED, false));
            row.addView(details, new LinearLayout.LayoutParams(0, -2, 1));
            TextView score = text(calculateScore(record.fps, record.temperature) + "/100", 12, ACCENT, true);
            row.addView(score);
            LinearLayout.LayoutParams params = matchWrap();
            params.bottomMargin = dp(7);
            parent.addView(row, params);
        }
    }

    private int calculateScore(double fps, double temperature) {
        int fpsPoints = fps >= 90 ? 60 : fps >= 60 ? 50 : fps >= 30 ? 35 : 15;
        int tempPoints = temperature <= 35 ? 40 : temperature <= 40 ? 30 : temperature <= 45 ? 20 : temperature <= 50 ? 10 : 0;
        return fpsPoints + tempPoints;
    }

    private String recommendation(double fps, double temperature) {
        if (temperature > 45) return "Temperatura elevada: deja descansar el equipo y reduce la carga gráfica.";
        if (fps < 30) return "FPS bajos: reduce la calidad gráfica y cierra aplicaciones en segundo plano.";
        if (fps >= 60 && temperature <= 40) return "Buen equilibrio registrado. Mantén una configuración estable.";
        return "Prueba reducir algunos efectos y vuelve a registrar una sesión para comparar.";
    }

    private void loadSessions() {
        String saved = getSharedPreferences("gamepulse_data", MODE_PRIVATE).getString("sessions", "[]");
        try {
            JSONArray array = new JSONArray(saved);
            for (int i = 0; i < array.length(); i++) {
                JSONObject item = array.getJSONObject(i);
                sessions.add(new SessionRecord(
                        item.optString("game", "Juego"),
                        item.optDouble("fps", 0),
                        item.optDouble("temperature", 0),
                        item.optLong("timestamp", System.currentTimeMillis())));
            }
        } catch (Exception ignored) {
            sessions.clear();
        }
    }

    private void saveSessions() {
        JSONArray array = new JSONArray();
        try {
            for (SessionRecord record : sessions) {
                JSONObject item = new JSONObject();
                item.put("game", record.game);
                item.put("fps", record.fps);
                item.put("temperature", record.temperature);
                item.put("timestamp", record.timestamp);
                array.put(item);
            }
            getSharedPreferences("gamepulse_data", MODE_PRIVATE).edit()
                    .putString("sessions", array.toString()).apply();
        } catch (Exception ignored) {
            Toast.makeText(this, "No se pudo guardar el historial.", Toast.LENGTH_SHORT).show();
        }
    }

    private void updateNavigation(boolean explore) {
        navExplore.setTextColor(explore ? ACCENT : MUTED);
        navPerformance.setTextColor(explore ? MUTED : ACCENT);
        navExplore.setTypeface(null, explore ? Typeface.BOLD : Typeface.NORMAL);
        navPerformance.setTypeface(null, explore ? Typeface.NORMAL : Typeface.BOLD);
    }

    private LinearLayout pageContainer() {
        LinearLayout page = new LinearLayout(this);
        page.setOrientation(LinearLayout.VERTICAL);
        page.setPadding(dp(20), dp(18), dp(20), dp(24));
        return page;
    }

    private void addScrollPage(LinearLayout page) {
        ScrollView scroll = new ScrollView(this);
        scroll.setFillViewport(false);
        scroll.setClipToPadding(false);
        scroll.addView(page);
        contentFrame.addView(scroll, new FrameLayout.LayoutParams(-1, -1));
    }

    private LinearLayout cardContainer() {
        LinearLayout card = new LinearLayout(this);
        card.setOrientation(LinearLayout.VERTICAL);
        card.setPadding(dp(16), dp(16), dp(16), dp(16));
        card.setBackground(roundBackground(SURFACE, STROKE, 18));
        return card;
    }

    private View metricCard(String value, String label) {
        LinearLayout card = new LinearLayout(this);
        card.setOrientation(LinearLayout.VERTICAL);
        card.setPadding(dp(11), dp(12), dp(9), dp(12));
        card.setBackground(roundBackground(SURFACE, STROKE, 15));
        card.addView(text(value, 17, ACCENT, true));
        TextView caption = text(label, 9, MUTED, true);
        LinearLayout.LayoutParams captionParams = matchWrap();
        captionParams.topMargin = dp(4);
        card.addView(caption, captionParams);
        return card;
    }

    private EditText input(String hint, int inputType) {
        EditText edit = new EditText(this);
        edit.setSingleLine(true);
        edit.setTextSize(14);
        edit.setTextColor(WHITE);
        edit.setHintTextColor(MUTED);
        edit.setHint(hint);
        edit.setInputType(inputType);
        edit.setPadding(dp(13), dp(9), dp(13), dp(9));
        edit.setBackground(roundBackground(SURFACE_LIGHT, STROKE, 12));
        return edit;
    }

    private TextView actionButton(String value) {
        TextView button = text(value, 14, BG, true);
        button.setGravity(Gravity.CENTER);
        button.setPadding(dp(15), dp(14), dp(15), dp(14));
        button.setBackground(roundBackground(ACCENT, ACCENT, 13));
        return button;
    }

    private void addText(LinearLayout parent, String value, float size, int color, boolean bold,
                         int left, int top, int right, int bottom) {
        TextView view = text(value, size, color, bold);
        LinearLayout.LayoutParams params = matchWrap();
        params.setMargins(dp(left), dp(top), dp(right), dp(bottom));
        parent.addView(view, params);
    }

    private TextView text(String value, float size, int color, boolean bold) {
        TextView view = new TextView(this);
        view.setText(value);
        view.setTextSize(size);
        view.setTextColor(color);
        if (bold) view.setTypeface(null, Typeface.BOLD);
        return view;
    }

    private GradientDrawable roundBackground(int fill, int stroke, int radius) {
        GradientDrawable drawable = new GradientDrawable();
        drawable.setColor(fill);
        drawable.setCornerRadius(dp(radius));
        drawable.setStroke(dp(1), stroke);
        return drawable;
    }

    private LinearLayout.LayoutParams matchWrap() {
        return new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
    }

    private LinearLayout.LayoutParams weightedParams(float weight, int left, int right) {
        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(0, ViewGroup.LayoutParams.WRAP_CONTENT, weight);
        params.setMargins(dp(left), 0, dp(right), 0);
        return params;
    }

    private int dp(float value) {
        return (int) (value * getResources().getDisplayMetrics().density + 0.5f);
    }

    private static class Game {
        String name, genre, shortName, load, description, tips, device, fps;
        Game(String name, String genre, String shortName, String load, String description,
             String tips, String device, String fps) {
            this.name = name;
            this.genre = genre;
            this.shortName = shortName;
            this.load = load;
            this.description = description;
            this.tips = tips;
            this.device = device;
            this.fps = fps;
        }
    }

    private static class SessionRecord {
        String game;
        double fps;
        double temperature;
        long timestamp;
        SessionRecord(String game, double fps, double temperature, long timestamp) {
            this.game = game;
            this.fps = fps;
            this.temperature = temperature;
            this.timestamp = timestamp;
        }
    }
}
