package io.github.foundationgames.automobility.screen;

import com.mojang.blaze3d.systems.RenderSystem;
import io.github.foundationgames.automobility.Automobility;
import io.github.foundationgames.automobility.recipe.AutoMechanicTableRecipe;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.client.resources.language.I18n;
import net.minecraft.client.resources.sounds.SimpleSoundInstance;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.util.FormattedCharSequence;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.item.crafting.Ingredient;
import org.lwjgl.glfw.GLFW;

import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class AutoMechanicTableScreen extends AbstractContainerScreen<AutoMechanicTableScreenHandler> {
    private static final ResourceLocation TEXTURE = Automobility.rl("textures/gui/container/auto_mechanic_table.png");

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
    private final List<ResourceLocation> orderedCategories = createDefaultCategories();
    private final Map<ResourceLocation, List<RecipeEntry>> recipes = new HashMap<>();

    private FormattedCharSequence categoryTitle;

    public AutoMechanicTableScreen(AutoMechanicTableScreenHandler handler, Inventory inventory, Component title) {
        super(handler, inventory, title);
        this.imageWidth = 176;
        this.imageHeight = 209;

        this.titleLabelY = 8;

        for (int id = 0; id < handler.recipes.size(); id++) {
            var recipe = handler.recipes.get(id);
            var category = recipe.getCategory();

            this.recipes.computeIfAbsent(category, cat -> new ArrayList<>());
            if (!this.orderedCategories.contains(category)) {
                this.orderedCategories.add(category);
            }

            this.recipes.get(category).add(new RecipeEntry(id, recipe));
        }

        this.inventoryLabelY = this.topPos + 115;
    }

    private static List<ResourceLocation> createDefaultCategories() {
        var list = new ArrayList<ResourceLocation>();
        list.add(Automobility.rl("frames"));
        list.add(Automobility.rl("engines"));
        list.add(Automobility.rl("wheels"));

        return list;
    }

    @Override
    protected void init() {
        super.init();

        this.recipePanelX = this.leftPos + 76;
        this.recipePanelY = this.topPos + 21;

        this.categoryButtonsX = this.leftPos + 75;
        this.categoryButtonsY = this.topPos + 4;

        this.categoryTitle = this.createCategoryTitle(this.orderedCategories.get(0));
    }

    @Override
    protected void containerTick() {
        super.containerTick();

        this.time++;
    }

    @Override
    public void render(GuiGraphics graphics, int mouseX, int mouseY, float delta) {
        super.render(graphics, mouseX, mouseY, delta);
        this.renderTooltip(graphics, mouseX, mouseY);
    }

    private void preDraw() {
        RenderSystem.setShaderColor(1, 1, 1, 1);
    }

    @Override
    protected void renderBg(GuiGraphics graphics, float delta, int mouseX, int mouseY) {
        this.renderBackground(graphics);

        this.preDraw();
        graphics.blit(TEXTURE, this.leftPos, this.topPos, 0, 0, this.imageWidth, this.imageHeight);
        this.drawCategoryBar(graphics, mouseX, mouseY);
        this.drawRecipes(graphics, mouseX, mouseY);

        this.drawMissingIngredients(graphics);
    }

    @Override
    protected void renderLabels(GuiGraphics graphics, int mouseX, int mouseY) {
        super.renderLabels(graphics, mouseX, mouseY);

        int hoveredRecipe = this.getHoveredRecipe(mouseX, mouseY);
        if (hoveredRecipe >= 0) {
            graphics.renderTooltip(font, this.menu.recipes.get(hoveredRecipe).getResultItem(), mouseX - this.leftPos, mouseY - this.topPos);
        }
    }

    private void changeCategory(int by) {
        this.currentCategory = Math.floorMod((this.currentCategory + by), this.orderedCategories.size());
        this.categoryTitle = createCategoryTitle(this.orderedCategories.get(this.currentCategory));
        this.recipeScroll = 0;
    }

    private FormattedCharSequence createCategoryTitle(ResourceLocation category) {
        var translated = I18n.get("part_category."+category.getNamespace()+"."+category.getPath());
        if (this.font.width(translated) > 64) {
            return Component.literal(this.font.plainSubstrByWidth(translated, 57) + "...").getVisualOrderText();
        }
        return Component.literal(this.font.plainSubstrByWidth(translated, 64)).getVisualOrderText();
    }

    private void buttonClicked() {
        if (this.minecraft != null) {
            this.minecraft.getSoundManager().play(SimpleSoundInstance.forUI(SoundEvents.UI_BUTTON_CLICK, 1.0F));
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
        this.menu.clickMenuButton(this.minecraft.player, id);
        this.minecraft.gameMode.handleInventoryButtonClick(this.menu.containerId, id);
    }

    private int getMaxRecipeScroll() {
        return Math.max(0, Mth.ceil((float)this.getRecipeList().size() / 5) - 3);
    }

    @Override
    public boolean mouseScrolled(double mouseX, double mouseY, double amount) {
        if (amount != 0 && this.getHoveredRecipe((int) mouseX, (int) mouseY) >= -1) {
            this.recipeScroll += amount > 0 ? -1 : 1;
            this.recipeScroll = Mth.clamp(this.recipeScroll, 0, this.getMaxRecipeScroll());

            return true;
        }

        return false;
    }

    protected final void drawMissingIngredient(GuiGraphics graphics, Ingredient ing, int x, int y) {
        graphics.fill(x, y, x + 16, y + 16, 0x45FF0000);

        var stacks = ing.getItems();
        graphics.renderFakeItem(stacks[Mth.floor((float)this.time / 30) % stacks.length], x, y);

        RenderSystem.depthFunc(516);
        graphics.fill(x, y, x + 16, y + 16, 0x30FFFFFF);
        RenderSystem.depthFunc(515);
    }

    protected void drawMissingIngredients(GuiGraphics graphics) {
        var inputInv = this.menu.inputInv;
        var missingIngs = new ArrayDeque<>(this.menu.missingIngredients);

        for (int i = 0; i < inputInv.getContainerSize(); i++) if (missingIngs.size() > 0) {
            int x = this.leftPos + 8 + (i * 18);
            int y = this.topPos + 88;

            if (inputInv.getItem(i).isEmpty()) {
                this.drawMissingIngredient(graphics, missingIngs.removeFirst(), x, y);
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
            int row = Mth.floor((float)mouseY / RECIPE_BUTTON_SIZE);
            int col = Mth.floor((float)mouseX / RECIPE_BUTTON_SIZE);

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

    protected void drawCategoryBar(GuiGraphics graphics, int mouseX, int mouseY) {
        int hoveredCatButton = this.getHoveredCategoryButton(mouseX, mouseY);

        this.preDraw();
        graphics.blit(TEXTURE, this.categoryButtonsX, this.categoryButtonsY,
                176, 17 + (hoveredCatButton < 0 ? CATEGORY_BUTTON_HEIGHT : 0), CATEGORY_BUTTON_WIDTH, CATEGORY_BUTTON_HEIGHT);
        graphics.blit(TEXTURE, this.categoryButtonsX + (CATEGORY_BUTTON_AREA_WIDTH - CATEGORY_BUTTON_WIDTH), this.categoryButtonsY,
                188, 17 + (hoveredCatButton > 0 ? CATEGORY_BUTTON_HEIGHT : 0), CATEGORY_BUTTON_WIDTH, CATEGORY_BUTTON_HEIGHT);

        if (this.categoryTitle != null) {
            graphics.drawCenteredString(this.font, this.categoryTitle, this.leftPos + 120, this.topPos + 8, 0xFFFFFF);
        }
    }

    protected void drawRecipes(GuiGraphics graphics, int mouseX, int mouseY) {
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
                        if (this.menu.getSelectedRecipe().isPresent() &&
                                this.menu.getSelectedRecipeId() == entry.id()) {
                            state = RecipeButtonState.SELECTED;
                        } else if (this.getHoveredRecipe(mouseX, mouseY) == entry.id()) {
                            state = RecipeButtonState.HOVERED;
                        }

                        this.drawRecipeEntry(entry, graphics, x, y, state);
                    } else {
                        break;
                    }
                }
            }
        }

        this.preDraw();
        int maxScroll = this.getMaxRecipeScroll();

        int scrollBarX = this.leftPos + 162;
        int scrollBarY = this.topPos + 21;
        if (maxScroll > 0) {
            scrollBarY += (int)((SCROLL_BAR_AREA_HEIGHT - SCROLL_BAR_HEIGHT) * ((float)this.recipeScroll / maxScroll));
        }

        graphics.blit(TEXTURE, scrollBarX, scrollBarY, 227, 0, SCROLL_BAR_WIDTH, SCROLL_BAR_HEIGHT);
    }

    protected void drawRecipeEntry(RecipeEntry entry, GuiGraphics graphics, int x, int y, RecipeButtonState state) {
        this.preDraw();
        graphics.blit(TEXTURE, x, y, 176 + (state.ordinal() * RECIPE_BUTTON_SIZE), 0, RECIPE_BUTTON_SIZE, RECIPE_BUTTON_SIZE);

        var stack = entry.recipe.getResultItem();
        graphics.renderFakeItem(stack, x, y);
    }

    public static record RecipeEntry(int id, AutoMechanicTableRecipe recipe) {}

    public enum RecipeButtonState {
        DEFAULT, HOVERED, SELECTED
    }
}
