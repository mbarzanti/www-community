<script>
  import PanelGroupItem from '../../UiKitLite/panelgrouppills/PanelGroupItem.svelte'

  export let groupId
  export let itemDescriptor
  export let noOpenButton = true
</script>

<PanelGroupItem
  groupId="accordion"
  stepIndex={itemDescriptor.stepIndex}
  stepName={itemDescriptor.stepName}
  stepTitle={itemDescriptor.title}
  markCompleted={itemDescriptor.isCompleted}
  isDisabled={!itemDescriptor.isCurrentStep}
  {noOpenButton}
  isCollapsed={!itemDescriptor.isCurrentStep}>
  <slot />
</PanelGroupItem>
