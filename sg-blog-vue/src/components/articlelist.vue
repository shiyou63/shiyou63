<!-- 文章列表 -->
<template>
  <div> <!-- 添加一个根元素来包裹所有内容 -->
    <el-row
      class="searchBox article-search"
      style="position: fixed; top: 20px; left: 50%; transform: translateX(-50%); width: 80%; max-width: 600px; z-index: 1000; background: rgba(255,255,255,0.9); padding: 10px; border-radius: 4px; box-shadow: 0 2px 12px rgba(0,0,0,0.1);"
    >
      <el-col :span="18">
        <el-input
          v-model="queryParams.search"
          placeholder="请输入文章标题"
          clearable
          :maxlength="10"
        ></el-input>
      </el-col>
      <el-col :span="6" style="text-align: right;">
        <el-button type="primary" @click="handleSearch">搜索</el-button>
      </el-col>
    </el-row>
    <!-- 添加顶部间距避免内容遮挡 -->
    <el-row class="sharelistBox" style="margin-top: 80px;">
      <!-- 原有列表代码保持不变 -->
    </el-row>
    <el-row class="sharelistBox">
      <el-col
        :span="24"
        class="s-item tcommonBox"
        v-for="(item,index) in sortedArticles"
        :key="'article'+index"
      >
        <!-- 添加置顶角标 -->
        <div v-if="item.isTop === 1" class="top-corner">TOP</div>
        <span class="s-round-date">
                <span class="month" v-html="showInitDate(item.createTime,'month')+'月'"></span>
                <span class="day" v-html="showInitDate(item.createTime,'date')"></span>
            </span>
        <header>
          <h1>
            <a :href="'#/DetailArticle?aid='+item.id" target="_blank">
              {{item.title}}
            </a>
          </h1>
          <h2>
            <i class="fa fa-fw fa-user"></i>发表于
            <i class="fa fa-fw fa-clock-o"></i><span v-html="showInitDate(item.createTime,'all')">{{showInitDate(item.createTime,'all')}}</span> •
            <i class="fa fa-fw fa-eye"></i>{{item.viewCount}} 次围观 •
          </h2>
          <div class="ui label">
            <a :href="'#/Share?classId='+item.categoryId">{{item.categoryName}}</a>
          </div>
        </header>
        <div class="article-content">
          <p style="text-indent:2em;">
            {{item.summary}}
          </p>
          <p  style="max-height:300px;overflow:hidden;text-align:center;">
            <img :src="item.thumbnail" alt="" class="maxW">
          </p>
        </div>
        <div class="viewdetail">
          <a class="tcolors-bg" :href="'#/DetailArticle?aid='+item.id" target="_blank">
            阅读全文>>
          </a>
        </div>
      </el-col>
      <el-col class="viewmore">
        <a v-show="hasMore" class="tcolors-bg" href="javascript:void(0);" @click="addMoreFun">点击加载更多</a>
        <a v-show="!hasMore" class="tcolors-bg" href="javascript:void(0);">暂无更多数据</a>
      </el-col>
    </el-row>
  </div>
</template>
<script>
import {initDate} from '../utils/server.js'
import {articleList} from '../api/article'
export default {
  name:'Share',
  data() { //选项 / 数据
    return {
      // 查询参数
      queryParams: {
        pageNum: 1,
        pageSize: 10,
        categoryId: 0,
        search: '', // 初始化搜索内容为空字符串
      },
      articleList:[],
      hasMore:true
    }
  },
  computed: {
    sortedArticles() {
      return this.articleList.slice().sort((a, b) => {
        // 优先按置顶排序
        if (a.isTop !== b.isTop) return b.isTop - a.isTop
        // 其次按时间倒序
        return new Date(b.createTime) - new Date(a.createTime)
      })
    }
  },
  methods: { //事件处理器
    showInitDate: function(oldDate,full){
      return initDate(oldDate,full)
    },
    handleSearch() {
      this.articleList = []
      this.getList()
    },
    getList(){
      articleList(this.queryParams).then((response)=>{
        this.articleList = this.articleList.concat(response.rows)
        if(response.total<=this.articleList.length){
          this.hasMore=false
        }else{
          this.hasMore=true
          this.queryParams.pageNum++
        }
        this.clearSearch()
      })
    },
    clearSearch() {
      this.queryParams.search = '';
    },
    showSearchShowList:function(initData){//展示数据
      if(initData){
        this.articleList = []
      }
      this.getList()
    },
    addMoreFun:function(){//查看更多
      this.showSearchShowList(false);
    },
    routeChange:function(){
      var that = this;
      this.queryParams.categoryId = (that.$route.query.classId==undefined?0:parseInt(that.$route.query.classId));//获取传参的classId
      this.showSearchShowList(true);
    }
  },
  components: { //定义组件
  },
  watch: {
    // 如果路由有变化，会再次执行该方法
    '$route':'routeChange',
    '$store.state.keywords':'routeChange'
  },
  created() { //生命周期函数
    // console.log(this.$route);
    var that = this;
    that.routeChange();
  }
}
</script>
<style scoped>
.top-corner {
  position: absolute;
  top: 5px;
  right: 5px;
  background: #ff4d4f;
  color: white;
  padding: 2px 8px;
  border-radius: 3px;
  font-size: 12px;
  z-index: 2;
}
@media (min-width: 926px) {
  .article-search {
    position: fixed;
    top: 0.2%;
    right: 7%;
    width: 50%;
    z-index: 999;
  }
}
@media (max-width: 926px) {
  .article-search {
    display: none;
  }
}
/* 添加或修改顶置角标样式 */
.top-corner {
  position: absolute;
  right: 0;
  top: 0;
  background: #ff4d4f;  /* 更醒目的颜色 */
  color: white;
  padding: 2px 8px;
  font-size: 12px;
  border-radius: 0 4px 0 4px;  /* 圆角方向调整 */
  z-index: 1;
}

/* 确保父容器允许绝对定位 */
.s-item.tcommonBox {
  position: relative;  /* 新增，确保角标相对于此容器定位 */
}

.shareclassTwo li{
  display: inline-block;
}
.shareclassTwo li a{
  display: inline-block;
  padding:3px 7px;
  margin:5px 10px;
  color:#fff;
  border-radius: 4px;
  background: #64609E;
  border: 1px solid #64609E;
  transition: transform 0.2s linear;
  -webkit-transition: transform 0.2s linear;
}
.shareclassTwo li a:hover{
  transform: translate(0,-3px);
  -webkit-transform: translate(0,-3px);
}
.shareclassTwo li a.active{
  background: #fff;
  color:#64609E;
}
/*文章列表*/
.sharelistBox{
  transition: all 0.5s ease-out;
  font-size: 15px;
}
</style>
