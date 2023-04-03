package io.github.foundationgames.automobility.screen;

import com.mojang.blaze3d.systems.RenderSystem;
import io.github.foundationgames.automobility.Automobility;
import io.github.foundationgames.automobility.recipe.AutoMechanicTableRecipe;
import net.minecraft.client.gui.DrawableHelper;
import net.minecraft.client.gui.screen.ingame.HandledScreen;
import net.minecraft.client.item.TooltipContext;
import net.minecraft.client.render.GameRenderer;
import net.minecraft.client.resource.language.I18n;
import net.minecraft.client.sound.PositionedSoundInstance;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.recipe.Ingredient;
import net.minecraft.sound.SoundEvents;
import net.minecraft.text.OrderedText;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.MathHelper;
import org.lwjgl.glfw.GLFW;

import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class AutoMechanicTableScreen extends HandledScreen<AutoMechanicTableScreenHandler> {
    private static final Identifier TEXTURE = Automobility.id("textures/gui/container/auto_mechanic_table.png");

    private static final int RECIPE_BUTTON_SIZE = 17;
    private static final int RECIPE_PANEL_WIDTH = 85;
    private static final int RECIPE_PANEL_HEIGHT = 51;

    private static final int CATEGORY_BUTTON_WIDTH = 12;
    private static final int CATEGORY_BUTTON_HEIGHT = 15;
    private static final int CATEGORY_BUTTON_AREA_WIDTH = 91;

    private static final int SCROLL_BAR_WIDTH = 3;
    private static final int SCROLL_BAR_HEIGHT = 10;
    private static final int SCROLL_BAR_AREA_HEIGHT = 51;

    private long time = 0;

    private int recipePanelX;
    private int recipePanelY;

    private int categoryButtonsX;
    private int categoryButtonsY;

    private int currentCategory = 0;
    private int recipeScroll = 0;
    private final List<Identifier> orderedCategories = createDefaultCategories();
    private final Map<Identifier, List<RecipeEntry>> recipes = new HashMap<>();

    private OrderedText categoryTitle;

    public AutoMechanicTableScreen(AutoMechanicTableScreenHandler handler, PlayerInventory inventory, Text title) {
        super(handler, inventory, title);
        this.backgroundWidth = 176;
        this.backgroundHeight = 209;

        this.titleY = 8;

        for (int id = 0; id < handler.recipes.size(); id++) {
            var recipe = handler.recipes.get(id);
            var category = recipe.getCategory();

            this.recipes.computeIfAbsent(category, cat -> new ArrayList<>());
            if (!this.orderedCategories.contains(category)) {
                this.orderedCategories.add(category);
            }

            this.recipes.get(category).add(new RecipeEntry(id, recipe));
        }

        this.playerInventoryTitleY = this.y + 115;
    }

    private static List<Identifier> createDefaultCategories() {
        var list = new ArrayList<Identifier>();
        list.add(Automobility.id("frames"));
        list.add(Automobility.id("engines"));
        list.add(Automobility.id("wheels"));

        return list;
    }

    @Override
    protected void init() {
        super.init();

        this.recipePanelX = this.x + 76;
        this.recipePanelY = this.y + 21;

        this.categoryButtonsX = this.x + 75;
        this.categoryButtonsY = this.y + 4;

        this.categoryTitle = this.createCategoryTitle(this.orderedCategories.get(0));
    }

    @Override
    protected void handledScreenTick() {
        super.handledScreenTick();

        this.time++;
    }

    @Override
    public void render(MatrixStack matrices, int mouseX, int mouseY, float delta) {
        super.render(matrices, mouseX, mouseY, delta);
        this.drawMouseoverTooltip(matrices, mouseX, mouseY);
    }

    private void preDraw() {
        RenderSystem.setShader(GameRenderer::getPositionTexProgram);
        RenderSystem.setShaderColor(1, 1, 1, 1);
        RenderSystem.setShaderTexture(0, TEXTURE);
    }

    @Override
    protected void drawBackground(MatrixStack matrices, float delta, int mouseX, int mouseY) {
        this.renderBackground(matrices);

        this.preDraw();
        this.drawTexture(matrices, this.x, this.y, 0, 0, this.backgroundWidth, this.backgroundHeight);
        this.drawCategoryBar(matrices, mouseX, mouseY);
        this.drawRecipes(matrices, mouseX, mouseY);

        this.drawMissingIngredients(matrices);
    }

    @Override
    protected void drawForeground(MatrixStack matrices, int mouseX, int mouseY) {
        super.drawForeground(matrices, mouseX, mouseY);

        int hoveredRecipe = this.getHoveredRecipe(mouseX, mouseY);
        if (hoveredRecipe >= 0) {
            this.renderTooltip(matrices, this.handler.recipes.get(hoveredRecipe).getOutput(), mouseX - this.x, mouseY - this.y);
        }
    }

    private void changeCategory(int by) {
        this.currentCategory = Math.floorMod((this.currentCategory + by), this.orderedCategories.size());
        this.categoryTitle = createCategoryTitle(this.orderedCategories.get(this.currentCategory));
        this.recipeScroll = 0;
    }

    private OrderedText createCategoryTitle(Identifier category) {
        var translated = I18n.translate("part_category."+category.getNamespace()+"."+category.getPath());
        if (this.textRenderer.getWidth(translated) > 64) {
            return Text.literal(this.textRenderer.trimToWidth(translated, 57) + "...").asOrderedText();
        }
        return Text.literal(this.textRenderer.trimToWidth(translated, 64)).asOrderedText();
    }

    private void buttonClicked() {
        if (this.client != null) {
            this.client.getSoundManager().play(PositionedSoundInstance.master(SoundEvents.UI_BUTTON_CLICK, 1.0F));
        }
    }

    @Override
    public boolean mouseClicked(double mouseX, double mouseY, int button) {
        if (button == GLFW.GLFW_MOUSE_BUTTON_1) {
            int selectedCatButton = getHoveredCategoryButton((int) mouseX, (int) mouseY);
            if (selectedCatButton != 0) {
                this.changeCategory(selectedCatButton);
                this.buttonClicked();

                return true;
            }

            int recipe = this.getHoveredRecipe((int) mouseX, (int) mouseY);
            if (recipe >= 0) {
                this.selectRecipe(recipe);
                this.buttonClicked();

                return true;
            }
        }
        return super.mouseClicked(mouseX, mouseY, button);
    }

    private void selectRecipe(int id) {
        this.handler.onButtonClick(this.client.player, id);
        this.client.interactionManager.clickButton(this.handler.syncId, id);
    }

    private int getMaxRecipeScroll() {
        return Math.max(0, MathHelper.ceil((float)this.getRecipeList().size() / 5) - 3);
    }

    @Override
    public boolean mouseScrolled(double mouseX, double mouseY, double amount) {
        if (amount != 0 && this.getHoveredRecipe((int) mouseX, (int) mouseY) >= -1) {
            this.recipeScroll += amount > 0 ? -1 : 1;
            this.recipeScroll = MathHelper.clamp(this.recipeScroll, 0, this.getMaxRecipeScroll());

            return true;
        }

        return false;
    }

    protected final void drawMissingIngredient(MatrixStack matrices, Ingredient ing, int x, int y) {
        DrawableHelper.fill(matrices, x, y, x + 16, y + 16, 0x45FF0000);

        var stacks = ing.getMatchingStacks();
        this.itemRenderer.renderInGui(stacks[MathHelper.floor((float)this.time / 30) % stacks.length], x, y);

        RenderSystem.depthFunc(516);
        DrawableHelper.fill(matrices, x, y, x + 16, y + 16, 0x30FFFFFF);
        RenderSystem.depthFunc(515);
    }

    protected void drawMissingIngredients(MatrixStack matrices) {
        var inputInv = this.handler.inputInv;
        var missingIngs = new ArrayDeque<>(this.handler.missingIngredients);

        for (int i = 0; i < inputInv.size(); i++) if (missingIngs.size() > 0) {
            int x = this.x + 8 + (i * 18);
            int y = this.y + 88;

            if (inputInv.getStack(i).isEmpty()) {
                this.drawMissingIngredient(matrices, missingIngs.removeFirst(), x, y);
            }
        }
    }

    protected List<RecipeEntry> getRecipeList() {
        if (this.currentCategory < this.orderedCategories.size() && this.currentCategory >= 0) {
            return this.recipes.get(this.orderedCategories.get(this.currentCategory));
        }

        return Collections.emptyList();
    }

    protected int getHoveredCategoryButton(int mouseX, int mouseY) {
        if (mouseY > this.categoryButtonsY && mouseY < this.categoryButtonsY + CATEGORY_BUTTON_HEIGHT) {
            int relX = mouseX - this.categoryButtonsX;
            if (relX < 0 || relX > CATEGORY_BUTTON_AREA_WIDTH) {
                return 0;
            }

            if (relX < CATEGORY_BUTTON_WIDTH) {
                return -1;
            }
            if (relX > (CATEGORY_BUTTON_AREA_WIDTH - CATEGORY_BUTTON_WIDTH)) {
                return 1;
            }
        }

        return 0;
    }

    protected int getHoveredRecipe(int mouseX, int mouseY) {
        mouseX -= this.recipePanelX;
        mouseY -= this.recipePanelY;

        if (this.currentCategory < this.orderedCategories.size() && this.currentCategory >= 0 &&
                (mouseX >= 0 && mouseX < RECIPE_PANEL_WIDTH) && (mouseY >= 0 && mouseY < RECIPE_PANEL_HEIGHT)) {
            int row = MathHelper.floor((float)mouseY / RECIPE_BUTTON_SIZE);
            int col = MathHelper.floor((float)mouseX / RECIPE_BUTTON_SIZE);

            if (row >= 0 && col >= 0) {
                int idx = (5 * (row + this.recipeScroll)) + col;
                var recipes = this.recipes.get(this.orderedCategories.get(this.currentCategory));
                if (idx < recipes.size()) {
                    return recipes.get(idx).id();
                }

                return -1; // Still within the recipe box bounds, but no recipe
            }
        }

        return -2;
    }

    protected void drawCategoryBar(MatrixStack matrices, int mouseX, int mouseY) {
        int hoveredCatButton = this.getHoveredCategoryButton(mouseX, mouseY);

        this.preDraw();
        this.drawTexture(matrices, this.categoryButtonsX, this.categoryButtonsY,
                176, 17 + (hoveredCatButton < 0 ? CATEGORY_BUTTON_HEIGHT : 0), CATEGORY_BUTTON_WIDTH, CATEGORY_BUTTON_HEIGHT);
        this.drawTexture(matrices, this.categoryButtonsX + (CATEGORY_BUTTON_AREA_WIDTH - CATEGORY_BUTTON_WIDTH), this.categoryButtonsY,
                188, 17 + (hoveredCatButton > 0 ? CATEGORY_BUTTON_HEIGHT : 0), CATEGORY_BUTTON_WIDTH, CATEGORY_BUTTON_HEIGHT);

        if (this.categoryTitle != null) {
            DrawableHelper.drawCenteredTextWithShadow(matrices, this.textRenderer, this.categoryTitle, this.x + 120, this.y + 8, 0xFFFFFF);
        }
    }

    protected void drawRecipes(MatrixStack matrices, int mouseX, int mouseY) {
        if (this.orderedCategories.size() > 0) {
            var recipes = this.recipes.get(this.orderedCategories.get(this.currentCategory));

            for (int row = 0; row < 3; row++) {
                for (int col = 0; col < 5; col++) {
                    int idx = (5 * this.recipeScroll) + (5 * row) + col;

                    if (idx < recipes.size()) {
                        int x = (col * RECIPE_BUTTON_SIZE) + this.recipePanelX;
                        int y = (row * RECIPE_BUTTON_SIZE) + this.recipePanelY;

                        var entry = recipes.get(idx);

                        var state = RecipeButtonState.DEFAULT;
                        if (this.handler.getSelectedRecipe().isPresent() &&
                                this.handler.getSelectedRecipeId() == entry.id()) {
                            state = RecipeButtonState.SELECTED;
                        } else if (this.getHoveredRecipe(mouseX, mouseY) == entry.id()) {
                            state = RecipeButtonState.HOVERED;
                        }

                        this.drawRecipeEntry(entry, matrices, x, y, state);
                    } else {
                        break;
                    }
                }
            }
        }

        this.preDraw();
        int maxScroll = this.getMaxRecipeScroll();

        int scrollBarX = this.x + 162;
        int scrollBarY = this.y + 21;
        if (maxScroll > 0) {
            scrollBarY += (int)((SCROLL_BAR_AREA_HEIGHT - SCROLL_BAR_HEIGHT) * ((float)this.recipeScroll / maxScroll));
        }

        this.drawTexture(matrices, scrollBarX, scrollBarY, 227, 0, SCROLL_BAR_WIDTH, SCROLL_BAR_HEIGHT);
    }

    protected void drawRecipeEntry(RecipeEntry entry, MatrixStack matrices, int x, int y, RecipeButtonState state) {
        this.preDraw();
        this.drawTexture(matrices, x, y, 176 + (state.ordinal() * RECIPE_BUTTON_SIZE), 0, RECIPE_BUTTON_SIZE, RECIPE_BUTTON_SIZE);

        var stack = entry.recipe.getOutput();
        this.itemRenderer.renderInGui(stack, x, y);
    }

    public static record RecipeEntry(int id, AutoMechanicTableRecipe recipe) {}

    public enum RecipeButtonState {
        DEFAULT, HOVERED, SELECTED
    }
}
