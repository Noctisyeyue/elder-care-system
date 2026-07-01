<template>
  <div class="page-content">
    <router-view v-slot="{ Component, route: r }">
      <template v-if="Component">
        <keep-alive>
          <component
            :is="Component"
            v-if="r.meta?.keepAlive"
            :key="r.path"
          />
        </keep-alive>
        <component
          :is="Component"
          v-if="!r.meta?.keepAlive"
          :key="r.path"
        />
      </template>
    </router-view>
  </div>
</template>

<script setup lang="ts">
defineOptions({ name: 'PageContent' })
</script>

<style scoped>
.page-content {
  flex: 1;
  padding: 16px;
  overflow: auto;
  background: var(--default-bg-color);
  min-height: calc(100vh - var(--header-height) - 40px);
}
</style>
