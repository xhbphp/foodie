package com.github.jaychenfe.controller;

import com.github.jaychenfe.pojo.Items;
import com.github.jaychenfe.pojo.ItemsImg;
import com.github.jaychenfe.pojo.ItemsParam;
import com.github.jaychenfe.pojo.ItemsSpec;
import com.github.jaychenfe.pojo.vo.CommentLevelCountsVO;
import com.github.jaychenfe.pojo.vo.ItemCommentVO;
import com.github.jaychenfe.pojo.vo.ItemInfoVO;
import com.github.jaychenfe.pojo.vo.SearchItemsVO;
import com.github.jaychenfe.pojo.vo.ShopCartVO;
import com.github.jaychenfe.service.ItemService;
import com.github.jaychenfe.utils.ApiResponse;
import com.github.jaychenfe.utils.PagedGridResult;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * @author jaychefe
 */
@Api(value = "商品接口", tags = {"商品信息展示的相关接口"})
@RestController
@RequestMapping("items")
public class ItemsController extends BaseController {

    private final ItemService itemService;

    @Autowired
    public ItemsController(ItemService itemService) {
        this.itemService = itemService;
    }

    @ApiOperation(value = "查询商品详情", notes = "查询商品详情")
    @GetMapping("/info/{itemId}")
    public ApiResponse info(@ApiParam(name = "itemId", value = "商品id", required = true)
                            @PathVariable String itemId) {

        if (StringUtils.isBlank(itemId)) {
            return ApiResponse.errorMsg(null);
        }

        Items item = itemService.queryItemById(itemId);
        List<ItemsImg> itemImgList = itemService.queryItemImgList(itemId);
        List<ItemsSpec> itemsSpecList = itemService.queryItemSpecList(itemId);
        ItemsParam itemsParam = itemService.queryItemParam(itemId);

        ItemInfoVO itemInfoVO = new ItemInfoVO();
        itemInfoVO.setItem(item);
        itemInfoVO.setItemImgList(itemImgList);
        itemInfoVO.setItemSpecList(itemsSpecList);
        itemInfoVO.setItemParams(itemsParam);

        return ApiResponse.ok(itemInfoVO);
    }

    @ApiOperation(value = "查询商品评价等级", notes = "查询商品评价等级")
    @GetMapping("/commentLevel")
    public ApiResponse commentLevel(
            @ApiParam(name = "itemId", value = "商品id", required = true)
            @RequestParam String itemId) {

        if (StringUtils.isBlank(itemId)) {
            return ApiResponse.errorMsg(null);
        }

        CommentLevelCountsVO countsVO = itemService.queryCommentCounts(itemId);

        return ApiResponse.ok(countsVO);
    }

    @ApiOperation(value = "查询商品评论", notes = "查询商品评论")
    @GetMapping("/comments")
    public ApiResponse comments(@ApiParam(name = "itemId", value = "商品id", required = true)
                                @RequestParam String itemId,
                                @ApiParam(name = "level", value = "评价等级")
                                @RequestParam Integer level,
                                @ApiParam(name = "page", value = "查询下一页的第几页")
                                @RequestParam(defaultValue = "1") Integer page,
                                @ApiParam(name = "pageSize", value = "分页的每一页显示的条数")
                                @RequestParam(defaultValue = "10") Integer pageSize) {

        if (StringUtils.isBlank(itemId)) {
            return ApiResponse.errorMsg(null);
        }

        PagedGridResult<ItemCommentVO> grid = itemService.queryPagedComments(itemId, level, page, pageSize);

        return ApiResponse.ok(grid);
    }

    @ApiOperation(value = "搜索商品列表", notes = "搜索商品列表")
    @GetMapping("/search")
    public ApiResponse search(@ApiParam(name = "keywords", value = "关键字", required = true)
                              @RequestParam String keywords,
                              @ApiParam(name = "sort", value = "排序")
                              @RequestParam String sort,
                              @ApiParam(name = "page", value = "查询下一页的第几页")
                              @RequestParam(defaultValue = "1") Integer page,
                              @ApiParam(name = "pageSize", value = "分页的每一页显示的条数")
                              @RequestParam(defaultValue = "20") Integer pageSize) {

        if (StringUtils.isBlank(keywords)) {
            return ApiResponse.errorMsg(null);
        }


        PagedGridResult<SearchItemsVO> grid = itemService.searchItems(keywords, sort, page, pageSize);

        return ApiResponse.ok(grid);
    }

    @ApiOperation(value = "通过分类id搜索商品列表", notes = "通过分类id搜索商品列表", httpMethod = "GET")
    @GetMapping("/catItems")
    public ApiResponse catItems(@ApiParam(name = "catId", value = "三级分类id", required = true)
                                @RequestParam Integer catId,
                                @ApiParam(name = "sort", value = "排序")
                                @RequestParam String sort,
                                @ApiParam(name = "page", value = "查询下一页的第几页")
                                @RequestParam(defaultValue = "1") Integer page,
                                @ApiParam(name = "pageSize", value = "分页的每一页显示的条数")
                                @RequestParam(defaultValue = "20") Integer pageSize) {

        if (catId == null) {
            return ApiResponse.errorMsg(null);
        }

        PagedGridResult<SearchItemsVO> grid = itemService.searchItems(catId, sort, page, pageSize);

        return ApiResponse.ok(grid);
    }

    /**
     * 用于用户长时间未登录网站，刷新购物车中的数据（主要是商品价格），类似京东淘宝
     *
     * @param itemSpecIds 商品规格拼接
     * @return ApiResponse
     */
    @ApiOperation(value = "根据商品规格ids查找最新的商品数据", notes = "根据商品规格ids查找最新的商品数据", httpMethod = "GET")
    @GetMapping("/refresh")
    public ApiResponse refresh(@ApiParam(name = "itemSpecIds", value = "拼接的规格ids", required = true, example = "1001,1003,1005")
                               @RequestParam String itemSpecIds) {

        if (StringUtils.isBlank(itemSpecIds)) {
            return ApiResponse.ok();
        }

        List<ShopCartVO> list = itemService.queryItemsBySpecIds(itemSpecIds);

        return ApiResponse.ok(list);
    }
}
